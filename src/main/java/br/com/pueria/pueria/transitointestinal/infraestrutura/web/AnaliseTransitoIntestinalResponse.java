package br.com.pueria.pueria.transitointestinal.infraestrutura.web;

import br.com.pueria.pueria.transitointestinal.aplicacao.AnaliseTransitoIntestinal;

import java.util.List;

public record AnaliseTransitoIntestinalResponse(
        String titulo,
        String resumo,
        String classificacaoFezes,
        List<String> rotina,
        List<String> conversaConsulta,
        List<String> habitosApoio
) {
    static AnaliseTransitoIntestinalResponse de(AnaliseTransitoIntestinal analise) {
        return new AnaliseTransitoIntestinalResponse(
                analise.titulo(),
                analise.resumo(),
                analise.classificacaoFezes(),
                analise.rotina(),
                analise.conversaConsulta(),
                analise.habitosApoio()
        );
    }
}
