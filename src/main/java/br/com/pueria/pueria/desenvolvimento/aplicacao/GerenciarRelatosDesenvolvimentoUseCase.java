package br.com.pueria.pueria.desenvolvimento.aplicacao;

import br.com.pueria.pueria.comum.excecao.RecursoNaoEncontradoException;
import br.com.pueria.pueria.desenvolvimento.dominio.RelatoDesenvolvimento;
import br.com.pueria.pueria.desenvolvimento.dominio.RelatoDesenvolvimentoRepositorio;
import br.com.pueria.pueria.desenvolvimento.dominio.TipoRelatoDesenvolvimento;
import br.com.pueria.pueria.responsaveis.dominio.VinculoResponsavelCriancaRepositorio;
import br.com.pueria.pueria.usuarios.dominio.Usuario;
import br.com.pueria.pueria.usuarios.dominio.UsuarioRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class GerenciarRelatosDesenvolvimentoUseCase {
    private final UsuarioRepositorio usuarioRepositorio;
    private final VinculoResponsavelCriancaRepositorio vinculoRepositorio;
    private final RelatoDesenvolvimentoRepositorio relatoRepositorio;

    public GerenciarRelatosDesenvolvimentoUseCase(UsuarioRepositorio usuarioRepositorio, VinculoResponsavelCriancaRepositorio vinculoRepositorio, RelatoDesenvolvimentoRepositorio relatoRepositorio) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.vinculoRepositorio = vinculoRepositorio;
        this.relatoRepositorio = relatoRepositorio;
    }

    @Transactional(readOnly = true)
    public List<RelatoDesenvolvimento> listar(UUID criancaId, String emailResponsavel) {
        validarAcesso(criancaId, emailResponsavel);
        return relatoRepositorio.listarPorCrianca(criancaId);
    }

    @Transactional
    public RelatoDesenvolvimento registrar(UUID criancaId, String emailResponsavel, TipoRelatoDesenvolvimento tipo, String descricao) {
        validarAcesso(criancaId, emailResponsavel);
        return relatoRepositorio.salvar(RelatoDesenvolvimento.registrar(criancaId, tipo, descricao));
    }

    @Transactional
    public void remover(UUID criancaId, UUID relatoId, String emailResponsavel) {
        validarAcesso(criancaId, emailResponsavel);
        RelatoDesenvolvimento relato = relatoRepositorio.buscarPorId(relatoId)
                .filter(item -> item.getCriancaId().equals(criancaId))
                .orElseThrow(() -> new RecursoNaoEncontradoException("Relato de desenvolvimento não encontrado."));
        relatoRepositorio.remover(relato);
    }

    private void validarAcesso(UUID criancaId, String emailResponsavel) {
        Usuario responsavel = usuarioRepositorio.buscarPorEmail(emailResponsavel).filter(Usuario::isAtivo)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Responsável autenticado não encontrado."));
        if (!vinculoRepositorio.usuarioPodeAcessarCrianca(responsavel.getId(), criancaId)) {
            throw new RecursoNaoEncontradoException("Criança não encontrada.");
        }
    }
}
