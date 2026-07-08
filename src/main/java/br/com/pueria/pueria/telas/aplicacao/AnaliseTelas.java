package br.com.pueria.pueria.telas.aplicacao;

import java.util.List;

public record AnaliseTelas(
        String titulo,
        String resumo,
        Integer minutosMediosDia,
        Integer minutosReferenciaMaximo,
        String classificacaoTempo,
        List<String> rotina,
        List<String> conversaConsulta,
        List<String> habitosApoio
) {}
