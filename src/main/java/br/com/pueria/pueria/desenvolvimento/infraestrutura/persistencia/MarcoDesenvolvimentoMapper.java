package br.com.pueria.pueria.desenvolvimento.infraestrutura.persistencia;

import br.com.pueria.pueria.desenvolvimento.dominio.MarcoDesenvolvimento;

public final class MarcoDesenvolvimentoMapper {
    private MarcoDesenvolvimentoMapper() {
    }

    public static MarcoDesenvolvimento paraDominio(MarcoDesenvolvimentoJpaEntidade entidade) {
        return MarcoDesenvolvimento.restaurar(
                entidade.getId(),
                entidade.getIdadeMeses(),
                entidade.getArea(),
                entidade.getDescricao(),
                entidade.getFonte(),
                entidade.isAtivo()
        );
    }
}
