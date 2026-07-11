package br.com.pueria.pueria.crescimento.dominio;

public enum IndicadorCurvaCrescimento {
    PESO_IDADE("Peso por idade", "kg"),
    COMPRIMENTO_IDADE("Comprimento/estatura por idade", "cm"),
    PERIMETRO_CEFALICO_IDADE("Perímetro cefálico por idade", "cm"),
    PESO_COMPRIMENTO("Peso por comprimento", "kg"),
    IMC_IDADE("IMC por idade", "kg/m²");

    private final String titulo;
    private final String unidade;

    IndicadorCurvaCrescimento(String titulo, String unidade) {
        this.titulo = titulo;
        this.unidade = unidade;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getUnidade() {
        return unidade;
    }
}
