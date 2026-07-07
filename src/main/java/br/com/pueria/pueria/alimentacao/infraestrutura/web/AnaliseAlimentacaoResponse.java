package br.com.pueria.pueria.alimentacao.infraestrutura.web;

import br.com.pueria.pueria.alimentacao.aplicacao.AnaliseAlimentacao;

import java.util.List;

public record AnaliseAlimentacaoResponse(
        String titulo,
        String resumo,
        List<String> rotina,
        List<String> conversaConsulta,
        List<String> habitosApoio
) {
    static AnaliseAlimentacaoResponse de(AnaliseAlimentacao analise) {
        return new AnaliseAlimentacaoResponse(
                analise.titulo(),
                analise.resumo(),
                analise.rotina(),
                analise.conversaConsulta(),
                analise.habitosApoio()
        );
    }
}
