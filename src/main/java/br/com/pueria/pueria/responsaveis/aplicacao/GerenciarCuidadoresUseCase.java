package br.com.pueria.pueria.responsaveis.aplicacao;

import br.com.pueria.pueria.comum.excecao.RecursoNaoEncontradoException;
import br.com.pueria.pueria.comum.excecao.RegraDominioException;
import br.com.pueria.pueria.criancas.dominio.CriancaRepositorio;
import br.com.pueria.pueria.responsaveis.dominio.ConviteCuidador;
import br.com.pueria.pueria.responsaveis.dominio.ConviteCuidadorRepositorio;
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
    private final ConviteCuidadorRepositorio convites;

    public GerenciarCuidadoresUseCase(UsuarioRepositorio usuarios, CriancaRepositorio criancas, VinculoResponsavelCriancaRepositorio vinculos, ConviteCuidadorRepositorio convites) { this.usuarios = usuarios; this.criancas = criancas; this.vinculos = vinculos; this.convites = convites; }

    @Transactional(readOnly = true)
    public List<CuidadorResumo> listar(UUID criancaId, String emailUsuario) {
        validarAcesso(criancaId, emailUsuario, false);
        return vinculos.listarPorCrianca(criancaId).stream().map(v -> { Usuario u = usuarios.buscarPorId(v.getUsuarioId()).orElseThrow(() -> new RecursoNaoEncontradoException("Cuidador não encontrado.")); return new CuidadorResumo(v.getId(), u.getNome(), u.getEmail(), v.getParentesco(), v.isPrincipal()); }).toList();
    }

    @Transactional
    public ConviteResumo convidar(UUID criancaId, String emailUsuario, String emailCuidador, Parentesco parentesco) {
        Usuario responsavel = validarAcesso(criancaId, emailUsuario, true);
        Usuario cuidador = usuarios.buscarPorEmail(emailCuidador == null ? "" : emailCuidador.trim().toLowerCase()).filter(Usuario::isAtivo).orElseThrow(() -> new RegraDominioException("Esta pessoa ainda não possui uma conta Pueria. Peça que ela se cadastre antes de convidar."));
        if (vinculos.usuarioPodeAcessarCrianca(cuidador.getId(), criancaId) || convites.existePendente(criancaId, cuidador.getId())) throw new RegraDominioException("Esta pessoa já acompanha a criança ou possui um convite pendente.");
        return resumoConvite(convites.salvar(ConviteCuidador.criar(criancaId, cuidador.getId(), responsavel.getId(), parentesco)));
    }

    @Transactional(readOnly = true)
    public List<ConviteResumo> listarConvitesPendentes(String emailUsuario) { return convites.listarPendentesPorConvidado(usuarioAtivo(emailUsuario).getId()).stream().map(this::resumoConvite).toList(); }

    @Transactional
    public void responderConvite(UUID conviteId, String emailUsuario, boolean aceitar) {
        Usuario usuario = usuarioAtivo(emailUsuario);
        ConviteCuidador convite = convites.buscarPorId(conviteId).filter(item -> item.getConvidadoUsuarioId().equals(usuario.getId())).orElseThrow(() -> new RecursoNaoEncontradoException("Convite não encontrado."));
        if (aceitar) { if (criancas.buscarPorId(convite.getCriancaId()).isEmpty()) throw new RecursoNaoEncontradoException("Criança não encontrada."); if (!vinculos.usuarioPodeAcessarCrianca(usuario.getId(), convite.getCriancaId())) vinculos.salvar(VinculoResponsavelCrianca.criarCuidador(usuario.getId(), convite.getCriancaId(), convite.getParentesco())); convites.salvar(convite.aceitar()); }
        else convites.salvar(convite.recusar());
    }

    @Transactional
    public void remover(UUID criancaId, UUID vinculoId, String emailUsuario) {
        validarAcesso(criancaId, emailUsuario, true);
        VinculoResponsavelCrianca vinculo = vinculos.buscarPorId(vinculoId).filter(item -> item.getCriancaId().equals(criancaId)).orElseThrow(() -> new RecursoNaoEncontradoException("Cuidador não encontrado."));
        if (vinculo.isPrincipal()) throw new RegraDominioException("O responsável principal não pode ser removido.");
        vinculos.removerPorId(vinculoId);
    }

    private Usuario validarAcesso(UUID criancaId, String emailUsuario, boolean exigirPrincipal) {
        Usuario usuario = usuarioAtivo(emailUsuario);
        boolean permitido = exigirPrincipal ? vinculos.usuarioEhResponsavelPrincipal(usuario.getId(), criancaId) : vinculos.usuarioPodeAcessarCrianca(usuario.getId(), criancaId);
        if (!permitido || criancas.buscarPorId(criancaId).isEmpty()) throw new RecursoNaoEncontradoException("Criança não encontrada.");
        return usuario;
    }
    private Usuario usuarioAtivo(String email) { return usuarios.buscarPorEmail(email).filter(Usuario::isAtivo).orElseThrow(() -> new RecursoNaoEncontradoException("Usuário autenticado não encontrado.")); }
    private ConviteResumo resumoConvite(ConviteCuidador c) { var crianca = criancas.buscarPorId(c.getCriancaId()).orElseThrow(() -> new RecursoNaoEncontradoException("Criança não encontrada.")); var criador = usuarios.buscarPorId(c.getCriadoPorUsuarioId()).orElseThrow(() -> new RecursoNaoEncontradoException("Responsável não encontrado.")); return new ConviteResumo(c.getId(), crianca.getId(), crianca.getNome(), criador.getNome(), c.getParentesco()); }
    public record CuidadorResumo(UUID id, String nome, String email, Parentesco parentesco, boolean principal) { }
    public record ConviteResumo(UUID id, UUID criancaId, String nomeCrianca, String convidadoPor, Parentesco parentesco) { }
}
