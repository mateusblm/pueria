package br.com.pueria.pueria.crescimento.aplicacao;

import br.com.pueria.pueria.crescimento.dominio.CurvaOmsCrescimentoService;
import br.com.pueria.pueria.crescimento.dominio.IndicadorCurvaCrescimento;
import br.com.pueria.pueria.crescimento.dominio.MedidaCrescimento;
import br.com.pueria.pueria.crescimento.dominio.MedidaCrescimentoRepositorio;
import br.com.pueria.pueria.crescimento.dominio.ResultadoCurvaCrescimento;
import br.com.pueria.pueria.criancas.dominio.Crianca;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ListarAvaliacoesCurvaCrescimentoUseCase {

    private final CrescimentoAcesso acesso;
    private final MedidaCrescimentoRepositorio medidaRepositorio;
    private final CurvaOmsCrescimentoService curvaOmsService;

    public ListarAvaliacoesCurvaCrescimentoUseCase(CrescimentoAcesso acesso, MedidaCrescimentoRepositorio medidaRepositorio, CurvaOmsCrescimentoService curvaOmsService) {
        this.acesso = acesso;
        this.medidaRepositorio = medidaRepositorio;
        this.curvaOmsService = curvaOmsService;
    }

    @Transactional(readOnly = true)
    public List<AvaliacaoCurvaCrescimento> executar(UUID criancaId, String emailResponsavel) {
        Crianca crianca = acesso.validar(criancaId, emailResponsavel);
        return medidaRepositorio.listarPorCrianca(criancaId)
                .stream()
                .map(medida -> avaliarMedida(crianca, medida))
                .toList();
    }

    private AvaliacaoCurvaCrescimento avaliarMedida(Crianca crianca, MedidaCrescimento medida) {
        IdadeParaCurva idade = calcularIdadeParaCurva(crianca, medida);
        List<ResultadoCurvaCrescimento> resultados = new ArrayList<>();

        curvaOmsService.avaliar(IndicadorCurvaCrescimento.PESO_IDADE, crianca.getSexo(), idade.idadeUsadaDias(), medida.getPesoKg())
                .ifPresent(resultados::add);
        curvaOmsService.avaliar(IndicadorCurvaCrescimento.COMPRIMENTO_IDADE, crianca.getSexo(), idade.idadeUsadaDias(), medida.getComprimentoCm())
                .ifPresent(resultados::add);
        curvaOmsService.avaliar(IndicadorCurvaCrescimento.PERIMETRO_CEFALICO_IDADE, crianca.getSexo(), idade.idadeUsadaDias(), medida.getPerimetroCefalicoCm())
                .ifPresent(resultados::add);

        return new AvaliacaoCurvaCrescimento(
                medida.getId(),
                medida.getDataMedicao(),
                idade.idadeUsadaDias(),
                idade.idadeCronologicaDias(),
                idade.corrigida(),
                idade.criterio(),
                List.copyOf(resultados)
        );
    }

    private IdadeParaCurva calcularIdadeParaCurva(Crianca crianca, MedidaCrescimento medida) {
        int idadeCronologicaDias = Math.max(0, Math.toIntExact(ChronoUnit.DAYS.between(crianca.getDataNascimento(), medida.getDataMedicao())));
        if (!crianca.isPrematura() || crianca.getSemanasGestacionais() == null) {
            return new IdadeParaCurva(idadeCronologicaDias, idadeCronologicaDias, false, "Idade cronológica");
        }

        int diasAntesDoTermo = Math.max(0, (40 - crianca.getSemanasGestacionais()) * 7);
        int limiteCorrecaoDias = deveEstenderCorrecaoAteTresAnos(crianca) ? 3 * 365 : 2 * 365;
        if (idadeCronologicaDias > limiteCorrecaoDias) {
            return new IdadeParaCurva(idadeCronologicaDias, idadeCronologicaDias, false, "Idade cronológica");
        }

        int idadeCorrigidaDias = Math.max(0, idadeCronologicaDias - diasAntesDoTermo);
        return new IdadeParaCurva(idadeCorrigidaDias, idadeCronologicaDias, true, "Idade corrigida para prematuridade");
    }

    private boolean deveEstenderCorrecaoAteTresAnos(Crianca crianca) {
        return crianca.getSemanasGestacionais() < 28
                || (crianca.getPesoNascimentoGramas() != null && crianca.getPesoNascimentoGramas() < 1000);
    }

    private record IdadeParaCurva(int idadeUsadaDias, int idadeCronologicaDias, boolean corrigida, String criterio) {}
}
