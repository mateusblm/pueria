package br.com.pueria.pueria.crescimento.infraestrutura.web;

import br.com.pueria.pueria.crescimento.aplicacao.AvaliacaoCurvaCrescimento;
import br.com.pueria.pueria.crescimento.dominio.ClassificacaoCurvaCrescimento;
import br.com.pueria.pueria.crescimento.dominio.IndicadorCurvaCrescimento;
import br.com.pueria.pueria.crescimento.dominio.ResultadoCurvaCrescimento;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record AvaliacaoCurvaCrescimentoResponse(
        UUID medidaId,
        LocalDate dataMedicao,
        int idadeDias,
        List<ResultadoCurvaResponse> resultados
) {
    static AvaliacaoCurvaCrescimentoResponse de(AvaliacaoCurvaCrescimento avaliacao) {
        return new AvaliacaoCurvaCrescimentoResponse(
                avaliacao.medidaId(),
                avaliacao.dataMedicao(),
                avaliacao.idadeDias(),
                avaliacao.resultados().stream().map(ResultadoCurvaResponse::de).toList()
        );
    }

    public record ResultadoCurvaResponse(
            IndicadorCurvaCrescimento indicador,
            String titulo,
            BigDecimal valor,
            String unidade,
            double zScore,
            double percentil,
            ClassificacaoCurvaCrescimento classificacao,
            String classificacaoTitulo,
            String classificacaoDetalhe,
            String fonte
    ) {
        static ResultadoCurvaResponse de(ResultadoCurvaCrescimento resultado) {
            return new ResultadoCurvaResponse(
                    resultado.indicador(),
                    resultado.indicador().getTitulo(),
                    resultado.valor(),
                    resultado.unidade(),
                    resultado.zScore(),
                    resultado.percentil(),
                    resultado.classificacao(),
                    resultado.classificacao().getTitulo(),
                    resultado.classificacao().getDetalhe(),
                    resultado.fonte()
            );
        }
    }
}
