package br.com.pueria.pueria.crescimento.dominio;

public enum ClassificacaoCurvaCrescimento {
    MUITO_ABAIXO("Muito abaixo da referência OMS", "Abaixo de -3 DP"),
    ABAIXO("Abaixo da referência OMS", "Entre -3 e -2 DP"),
    FAIXA_ESPERADA("Faixa esperada na referência OMS", "Entre -2 e +2 DP"),
    ACIMA("Acima da referência OMS", "Entre +2 e +3 DP"),
    MUITO_ACIMA("Muito acima da referência OMS", "Acima de +3 DP");

    private final String titulo;
    private final String detalhe;

    ClassificacaoCurvaCrescimento(String titulo, String detalhe) {
        this.titulo = titulo;
        this.detalhe = detalhe;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDetalhe() {
        return detalhe;
    }

    public static ClassificacaoCurvaCrescimento porZScore(double zScore) {
        if (zScore < -3.0) {
            return MUITO_ABAIXO;
        }
        if (zScore < -2.0) {
            return ABAIXO;
        }
        if (zScore <= 2.0) {
            return FAIXA_ESPERADA;
        }
        if (zScore <= 3.0) {
            return ACIMA;
        }
        return MUITO_ACIMA;
    }
}
