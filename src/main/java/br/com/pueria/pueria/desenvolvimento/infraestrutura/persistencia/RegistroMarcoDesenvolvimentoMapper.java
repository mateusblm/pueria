package br.com.pueria.pueria.desenvolvimento.infraestrutura.persistencia;

import br.com.pueria.pueria.desenvolvimento.dominio.RegistroMarcoDesenvolvimento;

public final class RegistroMarcoDesenvolvimentoMapper {
    private RegistroMarcoDesenvolvimentoMapper() {
    }

    public static RegistroMarcoDesenvolvimento paraDominio(RegistroMarcoDesenvolvimentoJpaEntidade entidade) {
        return RegistroMarcoDesenvolvimento.restaurar(
                entidade.getId(),
                entidade.getCriancaId(),
                entidade.getMarcoId(),
                entidade.getStatus(),
                entidade.getObservacao(),
                entidade.getRegistradoEm(),
                entidade.getAtualizadoEm()
        );
    }

    public static RegistroMarcoDesenvolvimentoJpaEntidade paraEntidade(RegistroMarcoDesenvolvimento registro) {
        return new RegistroMarcoDesenvolvimentoJpaEntidade(
                registro.getId(),
                registro.getCriancaId(),
                registro.getMarcoId(),
                registro.getStatus(),
                registro.getObservacao(),
                registro.getRegistradoEm(),
                registro.getAtualizadoEm()
        );
    }
}
