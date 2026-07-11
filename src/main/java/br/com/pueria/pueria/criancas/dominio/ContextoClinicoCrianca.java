package br.com.pueria.pueria.criancas.dominio;

import br.com.pueria.pueria.comum.excecao.RegraDominioException;

public record ContextoClinicoCrianca(
        TipoGestacao tipoGestacao,
        StatusCondicaoClinica statusT21,
        StatusCondicaoClinica statusTurner,
        boolean outraCondicaoRelevante,
        String observacoesCondicaoRelevante
) {
    public ContextoClinicoCrianca {
        tipoGestacao = tipoGestacao == null ? TipoGestacao.NAO_INFORMADO : tipoGestacao;
        statusT21 = statusT21 == null ? StatusCondicaoClinica.PREFIRO_INFORMAR_DEPOIS : statusT21;
        statusTurner = statusTurner == null ? StatusCondicaoClinica.PREFIRO_INFORMAR_DEPOIS : statusTurner;
        if (observacoesCondicaoRelevante == null || observacoesCondicaoRelevante.isBlank()) {
            observacoesCondicaoRelevante = null;
        } else {
            observacoesCondicaoRelevante = observacoesCondicaoRelevante.trim();
            if (observacoesCondicaoRelevante.length() > 1000) {
                throw new RegraDominioException("As observações da condição relevante devem ter no máximo 1000 caracteres.");
            }
        }
    }

    public static ContextoClinicoCrianca naoInformado() {
        return new ContextoClinicoCrianca(null, null, null, false, null);
    }
}
