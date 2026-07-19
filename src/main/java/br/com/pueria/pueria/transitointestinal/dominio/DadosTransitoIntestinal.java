package br.com.pueria.pueria.transitointestinal.dominio;

import java.time.LocalDate;

public record DadosTransitoIntestinal(
        LocalDate dataRegistro,
        TipoFezesBristol tipoFezes,
        Integer evacuacoesPorDia,
        Integer intervaloDiureseHoras,
        CorUrina corUrina,
        AspectoUrina aspectoUrina,
        CheiroUrina cheiroUrina,
        Boolean diureseSemAlteracoes,
        FacilidadeLimpezaFezes facilidadeLimpeza,
        Boolean muco,
        Boolean restosAlimentares,
        Boolean raiasSangue,
        Boolean constipacao,
        Boolean diarreia,
        Boolean dorEvacuar,
        Boolean escapeFecal,
        Boolean assaduraFrequente,
        Boolean assaduraVermelhidao,
        Boolean assaduraPontosVermelhos,
        Boolean preocupacaoFamilia,
        String observacao
) {
    public DadosTransitoIntestinal(
            LocalDate dataRegistro,
            TipoFezesBristol tipoFezes,
            Integer evacuacoesPorDia,
            FacilidadeLimpezaFezes facilidadeLimpeza,
            Boolean muco,
            Boolean restosAlimentares,
            Boolean raiasSangue,
            Boolean constipacao,
            Boolean diarreia,
            Boolean dorEvacuar,
            Boolean escapeFecal,
            Boolean assaduraFrequente,
            Boolean assaduraVermelhidao,
            Boolean assaduraPontosVermelhos,
            Boolean preocupacaoFamilia,
            String observacao
    ) {
        this(dataRegistro, tipoFezes, evacuacoesPorDia, null, null, null, null, null, facilidadeLimpeza,
                muco, restosAlimentares, raiasSangue, constipacao, diarreia, dorEvacuar, escapeFecal,
                assaduraFrequente, assaduraVermelhidao, assaduraPontosVermelhos, preocupacaoFamilia, observacao);
    }
}
