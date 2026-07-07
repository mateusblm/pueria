package br.com.pueria.pueria.sono.infraestrutura.web;

import br.com.pueria.pueria.sono.aplicacao.AnaliseSono;

import java.util.List;

public record AnaliseSonoResponse(
        String titulo,
        String resumo,
        Integer minutosSonoTotal24h,
        Integer minutosSonoEsperadoMinimo,
        Integer minutosSonoEsperadoMaximo,
        String classificacaoDuracao,
        List<String> rotina,
        List<String> conversaConsulta,
        List<String> habitosApoio
) {
    static AnaliseSonoResponse de(AnaliseSono analise) {
        return new AnaliseSonoResponse(
                analise.titulo(),
                analise.resumo(),
                analise.minutosSonoTotal24h(),
                analise.minutosSonoEsperadoMinimo(),
                analise.minutosSonoEsperadoMaximo(),
                analise.classificacaoDuracao(),
                analise.rotina(),
                analise.conversaConsulta(),
                analise.habitosApoio()
        );
    }
}
