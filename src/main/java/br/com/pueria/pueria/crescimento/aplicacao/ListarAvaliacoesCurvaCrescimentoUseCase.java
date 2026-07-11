package br.com.pueria.pueria.crescimento.aplicacao;

import br.com.pueria.pueria.crescimento.dominio.CurvaOmsCrescimentoService;
import br.com.pueria.pueria.crescimento.dominio.CurvaIntergrowthPrematuroService;
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
    private final CurvaIntergrowthPrematuroService curvaIntergrowthService;

    public ListarAvaliacoesCurvaCrescimentoUseCase(CrescimentoAcesso acesso, MedidaCrescimentoRepositorio medidaRepositorio, CurvaOmsCrescimentoService curvaOmsService, CurvaIntergrowthPrematuroService curvaIntergrowthService) {
        this.acesso = acesso;
        this.medidaRepositorio = medidaRepositorio;
        this.curvaOmsService = curvaOmsService;
        this.curvaIntergrowthService = curvaIntergrowthService;
    }

    public ListarAvaliacoesCurvaCrescimentoUseCase(CrescimentoAcesso acesso, MedidaCrescimentoRepositorio medidaRepositorio, CurvaOmsCrescimentoService curvaOmsService) {
        this(acesso, medidaRepositorio, curvaOmsService, new CurvaIntergrowthPrematuroService());
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

        curvaPara(idade, IndicadorCurvaCrescimento.PESO_IDADE, crianca.getSexo(), medida.getPesoKg())
                .ifPresent(resultados::add);
        curvaPara(idade, IndicadorCurvaCrescimento.COMPRIMENTO_IDADE, crianca.getSexo(), medida.getComprimentoCm())
                .ifPresent(resultados::add);
        curvaPara(idade, IndicadorCurvaCrescimento.PERIMETRO_CEFALICO_IDADE, crianca.getSexo(), medida.getPerimetroCefalicoCm())
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
            return new IdadeParaCurva(idadeCronologicaDias, idadeCronologicaDias, false, "Idade cronológica", false, 0);
        }

        int diasAntesDoTermo = Math.max(0, (40 - crianca.getSemanasGestacionais()) * 7);
        int limiteCorrecaoDias = deveEstenderCorrecaoAteTresAnos(crianca) ? 3 * 365 : 2 * 365;
        if (idadeCronologicaDias > limiteCorrecaoDias) {
            return new IdadeParaCurva(idadeCronologicaDias, idadeCronologicaDias, false, "Idade cronológica", true, crianca.getSemanasGestacionais());
        }

        int idadeCorrigidaDias = Math.max(0, idadeCronologicaDias - diasAntesDoTermo);
        return new IdadeParaCurva(idadeCorrigidaDias, idadeCronologicaDias, true, "OMS com idade corrigida para prematuridade", true, crianca.getSemanasGestacionais());
    }

    private java.util.Optional<ResultadoCurvaCrescimento> curvaPara(IdadeParaCurva idade, IndicadorCurvaCrescimento indicador, br.com.pueria.pueria.criancas.dominio.Sexo sexo, java.math.BigDecimal valor) {
        int idadePosMenstrualDias = idade.idadeCronologicaDias() + idade.semanasGestacionais() * 7;
        if (idade.prematura() && idadePosMenstrualDias >= 27 * 7 && idadePosMenstrualDias < 64 * 7) {
            return curvaIntergrowthService.avaliar(indicador, sexo, idadePosMenstrualDias, valor);
        }
        return curvaOmsService.avaliar(indicador, sexo, idade.idadeUsadaDias(), valor);
    }

    private boolean deveEstenderCorrecaoAteTresAnos(Crianca crianca) {
        return crianca.getSemanasGestacionais() < 28
                || (crianca.getPesoNascimentoGramas() != null && crianca.getPesoNascimentoGramas() < 1000);
    }

    private record IdadeParaCurva(int idadeUsadaDias, int idadeCronologicaDias, boolean corrigida, String criterio, boolean prematura, int semanasGestacionais) {}
}
