package br.com.pueria.pueria.saude.aplicacao;

import br.com.pueria.pueria.comum.excecao.RecursoNaoEncontradoException;
import br.com.pueria.pueria.criancas.dominio.CriancaRepositorio;
import br.com.pueria.pueria.responsaveis.dominio.VinculoResponsavelCriancaRepositorio;
import br.com.pueria.pueria.saude.dominio.DadosRegistroSaude;
import br.com.pueria.pueria.saude.dominio.RegistroSaude;
import br.com.pueria.pueria.saude.dominio.RegistroSaudeRepositorio;
import br.com.pueria.pueria.usuarios.dominio.Usuario;
import br.com.pueria.pueria.usuarios.dominio.UsuarioRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class GerenciarRegistrosSaudeUseCase {
    private final CriancaRepositorio criancas;
    private final UsuarioRepositorio usuarios;
    private final VinculoResponsavelCriancaRepositorio vinculos;
    private final RegistroSaudeRepositorio registros;

    public GerenciarRegistrosSaudeUseCase(CriancaRepositorio criancas, UsuarioRepositorio usuarios, VinculoResponsavelCriancaRepositorio vinculos, RegistroSaudeRepositorio registros) {
        this.criancas = criancas;
        this.usuarios = usuarios;
        this.vinculos = vinculos;
        this.registros = registros;
    }

    @Transactional(readOnly = true)
    public List<RegistroSaude> listar(UUID criancaId, String email) {
        validarAcesso(criancaId, email);
        return registros.listarPorCrianca(criancaId);
    }

    @Transactional
    public RegistroSaude registrar(UUID criancaId, DadosRegistroSaude dados, String email) {
        validarAcesso(criancaId, email);
        return registros.salvar(RegistroSaude.registrar(criancaId, dados));
    }

    @Transactional
    public RegistroSaude atualizar(UUID criancaId, UUID registroId, DadosRegistroSaude dados, String email) {
        validarAcesso(criancaId, email);
        RegistroSaude registro = buscarDaCrianca(criancaId, registroId);
        return registros.salvar(registro.atualizar(dados));
    }

    @Transactional
    public void remover(UUID criancaId, UUID registroId, String email) {
        validarAcesso(criancaId, email);
        buscarDaCrianca(criancaId, registroId);
        registros.removerPorId(registroId);
    }

    private RegistroSaude buscarDaCrianca(UUID criancaId, UUID registroId) {
        RegistroSaude registro = registros.buscarPorId(registroId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Registro de saúde não encontrado."));
        if (!registro.getCriancaId().equals(criancaId)) throw new RecursoNaoEncontradoException("Registro de saúde não encontrado.");
        return registro;
    }

    private void validarAcesso(UUID criancaId, String email) {
        Usuario usuario = usuarios.buscarPorEmail(email).filter(Usuario::isAtivo)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Responsável autenticado não encontrado."));
        if (!vinculos.usuarioPodeAcessarCrianca(usuario.getId(), criancaId) || criancas.buscarPorId(criancaId).isEmpty()) {
            throw new RecursoNaoEncontradoException("Criança não encontrada.");
        }
    }
}
