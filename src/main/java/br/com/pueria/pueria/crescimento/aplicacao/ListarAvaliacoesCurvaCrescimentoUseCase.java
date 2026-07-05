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
        int idadeDias = Math.toIntExact(ChronoUnit.DAYS.between(crianca.getDataNascimento(), medida.getDataMedicao()));
        List<ResultadoCurvaCrescimento> resultados = new ArrayList<>();

        curvaOmsService.avaliar(IndicadorCurvaCrescimento.PESO_IDADE, crianca.getSexo(), idadeDias, medida.getPesoKg())
                .ifPresent(resultados::add);
        curvaOmsService.avaliar(IndicadorCurvaCrescimento.COMPRIMENTO_IDADE, crianca.getSexo(), idadeDias, medida.getComprimentoCm())
                .ifPresent(resultados::add);
        curvaOmsService.avaliar(IndicadorCurvaCrescimento.PERIMETRO_CEFALICO_IDADE, crianca.getSexo(), idadeDias, medida.getPerimetroCefalicoCm())
                .ifPresent(resultados::add);

        return new AvaliacaoCurvaCrescimento(medida.getId(), medida.getDataMedicao(), idadeDias, List.copyOf(resultados));
    }
}
