package br.com.pueria.pueria.alimentacao.infraestrutura.web;

import br.com.pueria.pueria.alimentacao.dominio.AlimentoRegistroAlimentacao;
import br.com.pueria.pueria.alimentacao.dominio.GrupoAlimento;

public record AlimentoRegistroAlimentacaoResponse(
        String codigo,
        String nome,
        GrupoAlimento grupo
) {
    static AlimentoRegistroAlimentacaoResponse de(AlimentoRegistroAlimentacao alimento) {
        return new AlimentoRegistroAlimentacaoResponse(alimento.codigo(), alimento.nome(), alimento.grupo());
    }
}
