package br.com.pueria.pueria.transitointestinal.dominio;

import java.time.LocalDate;

public record DadosTransitoIntestinal(
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
) {}
