package br.com.pueria.pueria.telas.infraestrutura.web;

import br.com.pueria.pueria.telas.aplicacao.AnaliseTelas;

import java.util.List;

public record AnaliseTelasResponse(
        String titulo,
        String resumo,
        Integer minutosMediosDia,
        Integer minutosReferenciaMaximo,
        String classificacaoTempo,
        List<String> rotina,
        List<String> conversaConsulta,
        List<String> habitosApoio
) {
    static AnaliseTelasResponse de(AnaliseTelas analise) {
        return new AnaliseTelasResponse(
                analise.titulo(),
                analise.resumo(),
                analise.minutosMediosDia(),
                analise.minutosReferenciaMaximo(),
                analise.classificacaoTempo(),
                analise.rotina(),
                analise.conversaConsulta(),
                analise.habitosApoio()
        );
    }
}
