package br.com.pueria.pueria.alimentacao.aplicacao;

import java.util.List;

public record AnaliseAlimentacao(
        String titulo,
        String resumo,
        List<String> rotina,
        List<String> conversaConsulta,
        List<String> habitosApoio
) {}
