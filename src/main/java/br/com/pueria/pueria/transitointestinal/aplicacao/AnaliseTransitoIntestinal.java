package br.com.pueria.pueria.transitointestinal.aplicacao;

import java.util.List;

public record AnaliseTransitoIntestinal(
        String titulo,
        String resumo,
        String classificacaoFezes,
        List<String> rotina,
        List<String> conversaConsulta,
        List<String> habitosApoio
) {}
