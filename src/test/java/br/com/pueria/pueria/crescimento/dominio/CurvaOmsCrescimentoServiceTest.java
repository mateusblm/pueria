package br.com.pueria.pueria.crescimento.dominio;

import br.com.pueria.pueria.criancas.dominio.Sexo;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CurvaOmsCrescimentoServiceTest {

    private final CurvaOmsCrescimentoService service = new CurvaOmsCrescimentoService();

    @Test
    void deveCalcularZScoreZeroParaMedianaOmsDePesoAoNascerMenino() {
        ResultadoCurvaCrescimento resultado = service.avaliar(
                IndicadorCurvaCrescimento.PESO_IDADE,
                Sexo.MASCULINO,
                0,
                new BigDecimal("3.3464")
        ).orElseThrow();

        assertEquals(0.0, resultado.zScore(), 0.001);
        assertEquals(50.0, resultado.percentil(), 0.01);
        assertEquals(ClassificacaoCurvaCrescimento.FAIXA_ESPERADA, resultado.classificacao());
    }

    @Test
    void deveCalcularZScoreZeroParaMedianaOmsDeComprimentoAosCemDiasMenina() {
        ResultadoCurvaCrescimento resultado = service.avaliar(
                IndicadorCurvaCrescimento.COMPRIMENTO_IDADE,
                Sexo.FEMININO,
                100,
                new BigDecimal("60.4958")
        ).orElseThrow();

        assertEquals(0.0, resultado.zScore(), 0.001);
        assertEquals(50.0, resultado.percentil(), 0.01);
    }

    @Test
    void deveRetornarVazioQuandoSexoNaoFoiInformado() {
        assertTrue(service.avaliar(
                IndicadorCurvaCrescimento.PESO_IDADE,
                Sexo.NAO_INFORMADO,
                0,
                new BigDecimal("3.3")
        ).isEmpty());
    }

    @Test
    void deveCalcularPesoPorComprimentoComTabelaOficialOms() {
        ResultadoCurvaCrescimento resultado = service.avaliarPesoPorComprimento(
                Sexo.MASCULINO,
                new BigDecimal("68.0"),
                new BigDecimal("7.9674"),
                100
        ).orElseThrow();

        assertEquals(IndicadorCurvaCrescimento.PESO_COMPRIMENTO, resultado.indicador());
        assertEquals(0.0, resultado.zScore(), 0.001);
        assertEquals(50.0, resultado.percentil(), 0.01);
        assertEquals(100, resultado.idadeDias());
    }

    @Test
    void deveCalcularImcPorIdadeComTabelaOficialOms() {
        ResultadoCurvaCrescimento resultado = service.avaliar(
                IndicadorCurvaCrescimento.IMC_IDADE,
                Sexo.FEMININO,
                100,
                new BigDecimal("16.4673")
        ).orElseThrow();

        assertEquals(0.0, resultado.zScore(), 0.001);
        assertEquals(50.0, resultado.percentil(), 0.01);
    }

    @Test
    void naoDeveCalcularPesoPorComprimentoForaDaFaixaOficial() {
        assertTrue(service.avaliarPesoPorComprimento(
                Sexo.FEMININO,
                new BigDecimal("44.5"),
                new BigDecimal("2.5"),
                10
        ).isEmpty());
    }
}
