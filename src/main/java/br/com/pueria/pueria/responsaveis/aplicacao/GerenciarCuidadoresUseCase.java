package br.com.pueria.pueria.responsaveis.aplicacao;

import br.com.pueria.pueria.comum.excecao.RecursoNaoEncontradoException;
import br.com.pueria.pueria.comum.excecao.RegraDominioException;
import br.com.pueria.pueria.criancas.dominio.CriancaRepositorio;
import br.com.pueria.pueria.responsaveis.dominio.Parentesco;
import br.com.pueria.pueria.responsaveis.dominio.VinculoResponsavelCrianca;
import br.com.pueria.pueria.responsaveis.dominio.VinculoResponsavelCriancaRepositorio;
import br.com.pueria.pueria.usuarios.dominio.Usuario;
import br.com.pueria.pueria.usuarios.dominio.UsuarioRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class GerenciarCuidadoresUseCase {
    private final UsuarioRepositorio usuarios;
    private final CriancaRepositorio criancas;
    private final VinculoResponsavelCriancaRepositorio vinculos;

    public GerenciarCuidadoresUseCase(UsuarioRepositorio usuarios, CriancaRepositorio criancas, VinculoResponsavelCriancaRepositorio vinculos) {
        this.usuarios = usuarios;
        this.criancas = criancas;
        this.vinculos = vinculos;
    }

    @Transactional(readOnly = true)
    public List<CuidadorResumo> listar(UUID criancaId, String emailUsuario) {
        validarAcesso(criancaId, emailUsuario, false);
        return vinculos.listarPorCrianca(criancaId).stream()
                .map(vinculo -> {
                    Usuario usuario = usuarios.buscarPorId(vinculo.getUsuarioId())
                            .orElseThrow(() -> new RecursoNaoEncontradoException("Cuidador não encontrado."));
                    return new CuidadorResumo(vinculo.getId(), usuario.getNome(), usuario.getEmail(), vinculo.getParentesco(), vinculo.isPrincipal());
                })
                .toList();
    }

    @Transactional
    public CuidadorResumo convidar(UUID criancaId, String emailUsuario, String emailCuidador, Parentesco parentesco) {
        validarAcesso(criancaId, emailUsuario, true);
        String emailNormalizado = emailCuidador == null ? "" : emailCuidador.trim().toLowerCase();
        Usuario cuidador = usuarios.buscarPorEmail(emailNormalizado)
                .filter(Usuario::isAtivo)
                .orElseThrow(() -> new RegraDominioException("Esta pessoa ainda não possui uma conta Pueria. Peça que ela se cadastre antes de convidar."));
        if (vinculos.usuarioPodeAcessarCrianca(cuidador.getId(), criancaId)) {
            throw new RegraDominioException("Esta pessoa já acompanha a criança.");
        }
        VinculoResponsavelCrianca vinculo = vinculos.salvar(VinculoResponsavelCrianca.criarCuidador(cuidador.getId(), criancaId, parentesco == null ? Parentesco.OUTRO : parentesco));
        return new CuidadorResumo(vinculo.getId(), cuidador.getNome(), cuidador.getEmail(), vinculo.getParentesco(), false);
    }

    @Transactional
    public void remover(UUID criancaId, UUID vinculoId, String emailUsuario) {
        validarAcesso(criancaId, emailUsuario, true);
        VinculoResponsavelCrianca vinculo = vinculos.buscarPorId(vinculoId)
                .filter(item -> item.getCriancaId().equals(criancaId))
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cuidador não encontrado."));
        if (vinculo.isPrincipal()) {
            throw new RegraDominioException("O responsável principal não pode ser removido.");
        }
        vinculos.removerPorId(vinculoId);
    }

    private void validarAcesso(UUID criancaId, String emailUsuario, boolean exigirPrincipal) {
        Usuario usuario = usuarios.buscarPorEmail(emailUsuario).filter(Usuario::isAtivo)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário autenticado não encontrado."));
        boolean permitido = exigirPrincipal
                ? vinculos.usuarioEhResponsavelPrincipal(usuario.getId(), criancaId)
                : vinculos.usuarioPodeAcessarCrianca(usuario.getId(), criancaId);
        if (!permitido || criancas.buscarPorId(criancaId).isEmpty()) {
            throw new RecursoNaoEncontradoException("Criança não encontrada.");
        }
    }

    public record CuidadorResumo(UUID id, String nome, String email, Parentesco parentesco, boolean principal) { }
}
