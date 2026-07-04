package br.com.pueria.pueria.desenvolvimento.aplicacao;

import br.com.pueria.pueria.comum.excecao.RecursoNaoEncontradoException;
import br.com.pueria.pueria.criancas.dominio.Crianca;
import br.com.pueria.pueria.criancas.dominio.CriancaRepositorio;
import br.com.pueria.pueria.desenvolvimento.dominio.MarcoDesenvolvimento;
import br.com.pueria.pueria.desenvolvimento.dominio.MarcoDesenvolvimentoRepositorio;
import br.com.pueria.pueria.desenvolvimento.dominio.RegistroMarcoDesenvolvimento;
import br.com.pueria.pueria.desenvolvimento.dominio.RegistroMarcoDesenvolvimentoRepositorio;
import br.com.pueria.pueria.desenvolvimento.dominio.StatusMarcoDesenvolvimento;
import br.com.pueria.pueria.responsaveis.dominio.VinculoResponsavelCriancaRepositorio;
import br.com.pueria.pueria.usuarios.dominio.Usuario;
import br.com.pueria.pueria.usuarios.dominio.UsuarioRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.List;

@Service
public class ListarMarcosDesenvolvimentoUseCase {

    private static final int IDADE_MAXIMA_MARCOS_MESES = 60;

    private final CriancaRepositorio criancaRepositorio;
    private final UsuarioRepositorio usuarioRepositorio;
    private final VinculoResponsavelCriancaRepositorio vinculoRepositorio;
    private final MarcoDesenvolvimentoRepositorio marcoRepositorio;
    private final RegistroMarcoDesenvolvimentoRepositorio registroRepositorio;

    public ListarMarcosDesenvolvimentoUseCase(
            CriancaRepositorio criancaRepositorio,
            UsuarioRepositorio usuarioRepositorio,
            VinculoResponsavelCriancaRepositorio vinculoRepositorio,
            MarcoDesenvolvimentoRepositorio marcoRepositorio,
            RegistroMarcoDesenvolvimentoRepositorio registroRepositorio
    ) {
        this.criancaRepositorio = criancaRepositorio;
        this.usuarioRepositorio = usuarioRepositorio;
        this.vinculoRepositorio = vinculoRepositorio;
        this.marcoRepositorio = marcoRepositorio;
        this.registroRepositorio = registroRepositorio;
    }

    @Transactional(readOnly = true)
    public List<MarcoDesenvolvimentoResumo> executar(UUID criancaId, String emailResponsavel) {
        validarAcesso(criancaId, emailResponsavel);
        Crianca crianca = criancaRepositorio.buscarPorId(criancaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Criança não encontrada."));

        int idadeMeses = Math.min(calcularIdadeMeses(crianca), IDADE_MAXIMA_MARCOS_MESES);
        Map<UUID, RegistroMarcoDesenvolvimento> registros = registroRepositorio.listarPorCrianca(criancaId)
                .stream()
                .collect(Collectors.toMap(RegistroMarcoDesenvolvimento::getMarcoId, registro -> registro));

        return marcoRepositorio.listarAtivosAteIdadeMeses(idadeMeses)
                .stream()
                .map(marco -> paraResumo(marco, registros.get(marco.getId())))
                .toList();
    }

    private void validarAcesso(UUID criancaId, String emailResponsavel) {
        Usuario responsavel = usuarioRepositorio.buscarPorEmail(emailResponsavel)
                .filter(Usuario::isAtivo)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Responsável autenticado não encontrado."));

        if (!vinculoRepositorio.usuarioPodeAcessarCrianca(responsavel.getId(), criancaId)) {
            throw new RecursoNaoEncontradoException("Criança não encontrada.");
        }
    }

    private static int calcularIdadeMeses(Crianca crianca) {
        Period periodo = Period.between(crianca.getDataNascimento(), LocalDate.now());
        return Math.max(0, periodo.getYears() * 12 + periodo.getMonths());
    }

    private static MarcoDesenvolvimentoResumo paraResumo(MarcoDesenvolvimento marco, RegistroMarcoDesenvolvimento registro) {
        return new MarcoDesenvolvimentoResumo(
                marco.getId(),
                marco.getIdadeMeses(),
                marco.getArea(),
                marco.getDescricao(),
                marco.getFonte(),
                registro == null ? StatusMarcoDesenvolvimento.NAO_AVALIADO : registro.getStatus(),
                registro == null ? null : registro.getObservacao(),
                registro == null ? null : registro.getRegistradoEm()
        );
    }
}
