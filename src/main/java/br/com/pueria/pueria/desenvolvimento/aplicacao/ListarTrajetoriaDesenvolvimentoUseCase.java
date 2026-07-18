package br.com.pueria.pueria.desenvolvimento.aplicacao;

import br.com.pueria.pueria.comum.excecao.RecursoNaoEncontradoException;
import br.com.pueria.pueria.desenvolvimento.dominio.HistoricoRespostaMarcoDesenvolvimento;
import br.com.pueria.pueria.desenvolvimento.dominio.HistoricoRespostaMarcoDesenvolvimentoRepositorio;
import br.com.pueria.pueria.desenvolvimento.dominio.MarcoDesenvolvimentoRepositorio;
import br.com.pueria.pueria.desenvolvimento.dominio.StatusMarcoDesenvolvimento;
import br.com.pueria.pueria.responsaveis.dominio.VinculoResponsavelCriancaRepositorio;
import br.com.pueria.pueria.usuarios.dominio.Usuario;
import br.com.pueria.pueria.usuarios.dominio.UsuarioRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class ListarTrajetoriaDesenvolvimentoUseCase {

    private final UsuarioRepositorio usuarioRepositorio;
    private final VinculoResponsavelCriancaRepositorio vinculoRepositorio;
    private final HistoricoRespostaMarcoDesenvolvimentoRepositorio historicoRepositorio;
    private final MarcoDesenvolvimentoRepositorio marcoRepositorio;

    public ListarTrajetoriaDesenvolvimentoUseCase(
            UsuarioRepositorio usuarioRepositorio,
            VinculoResponsavelCriancaRepositorio vinculoRepositorio,
            HistoricoRespostaMarcoDesenvolvimentoRepositorio historicoRepositorio,
            MarcoDesenvolvimentoRepositorio marcoRepositorio
    ) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.vinculoRepositorio = vinculoRepositorio;
        this.historicoRepositorio = historicoRepositorio;
        this.marcoRepositorio = marcoRepositorio;
    }

    @Transactional(readOnly = true)
    public List<EventoTrajetoriaDesenvolvimentoResumo> executar(UUID criancaId, String emailResponsavel) {
        validarAcesso(criancaId, emailResponsavel);

        return historicoRepositorio.listarPorCrianca(criancaId).stream()
                .filter(evento -> evento.statusNovo() == StatusMarcoDesenvolvimento.OBSERVADO)
                .map(this::paraResumo)
                .flatMap(java.util.Optional::stream)
                .sorted(Comparator.comparing(EventoTrajetoriaDesenvolvimentoResumo::registradoEm).reversed())
                .limit(4)
                .toList();
    }

    private java.util.Optional<EventoTrajetoriaDesenvolvimentoResumo> paraResumo(HistoricoRespostaMarcoDesenvolvimento evento) {
        return marcoRepositorio.buscarPorId(evento.marcoId())
                .map(marco -> new EventoTrajetoriaDesenvolvimentoResumo(
                        tipoDo(evento),
                        marco.getDescricao(),
                        marco.getArea(),
                        evento.registradoEm()
                ));
    }

    private String tipoDo(HistoricoRespostaMarcoDesenvolvimento evento) {
        if (evento.statusAnterior() == null) {
            return "PRIMEIRA_OBSERVACAO";
        }
        if (evento.statusAnterior() == StatusMarcoDesenvolvimento.OBSERVADO) {
            return "OBSERVADO_NOVAMENTE";
        }
        return "NOVA_OBSERVACAO";
    }

    private void validarAcesso(UUID criancaId, String emailResponsavel) {
        Usuario responsavel = usuarioRepositorio.buscarPorEmail(emailResponsavel)
                .filter(Usuario::isAtivo)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Responsável autenticado não encontrado."));

        if (!vinculoRepositorio.usuarioPodeAcessarCrianca(responsavel.getId(), criancaId)) {
            throw new RecursoNaoEncontradoException("Criança não encontrada.");
        }
    }
}
