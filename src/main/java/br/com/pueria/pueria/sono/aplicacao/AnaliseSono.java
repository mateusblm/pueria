package br.com.pueria.pueria.sono.aplicacao;

import java.util.List;

public record AnaliseSono(
        String titulo,
        String resumo,
        Integer minutosSonoTotal24h,
        Integer minutosSonoEsperadoMinimo,
        Integer minutosSonoEsperadoMaximo,
        String classificacaoDuracao,
        List<String> rotina,
        List<String> conversaConsulta,
        List<String> habitosApoio
) {}
