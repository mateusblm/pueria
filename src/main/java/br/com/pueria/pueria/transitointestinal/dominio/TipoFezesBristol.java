package br.com.pueria.pueria.transitointestinal.dominio;

public enum TipoFezesBristol {
    TIPO_1,
    TIPO_2,
    TIPO_3,
    TIPO_4,
    TIPO_5,
    TIPO_6,
    TIPO_7,
    NAO_INFORMADO;

    public String descricaoParaResumo() {
        return switch (this) {
            case TIPO_1 -> "Tipo 1 (bolinhas endurecidas)";
            case TIPO_2 -> "Tipo 2 (alongada e endurecida)";
            case TIPO_3 -> "Tipo 3 (alongada com fissuras)";
            case TIPO_4 -> "Tipo 4 (macia e lisa)";
            case TIPO_5 -> "Tipo 5 (pedaços macios)";
            case TIPO_6 -> "Tipo 6 (pastosa ou amolecida)";
            case TIPO_7 -> "Tipo 7 (líquida)";
            case NAO_INFORMADO -> "Consistência não informada";
        };
    }
}
