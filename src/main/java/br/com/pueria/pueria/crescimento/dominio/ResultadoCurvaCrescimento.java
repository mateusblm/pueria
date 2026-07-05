package br.com.pueria.pueria.crescimento.dominio;

import java.math.BigDecimal;

public record ResultadoCurvaCrescimento(
        IndicadorCurvaCrescimento indicador,
        BigDecimal valor,
        String unidade,
        int idadeDias,
        double zScore,
        double percentil,
        ClassificacaoCurvaCrescimento classificacao,
        String fonte
) {}
