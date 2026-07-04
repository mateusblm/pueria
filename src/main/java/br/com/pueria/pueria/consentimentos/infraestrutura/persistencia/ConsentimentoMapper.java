package br.com.pueria.pueria.consentimentos.infraestrutura.persistencia;

import br.com.pueria.pueria.consentimentos.dominio.Consentimento;

final class ConsentimentoMapper {

    private ConsentimentoMapper() {
    }

    static ConsentimentoJpaEntidade paraEntidade(Consentimento consentimento) {
        ConsentimentoJpaEntidade entidade = new ConsentimentoJpaEntidade();
        entidade.setId(consentimento.getId());
        entidade.setUsuarioId(consentimento.getUsuarioId());
        entidade.setCriancaId(consentimento.getCriancaId());
        entidade.setTipo(consentimento.getTipo());
        entidade.setVersaoTermo(consentimento.getVersaoTermo());
        entidade.setAceito(consentimento.isAceito());
        entidade.setDataAceite(consentimento.getDataAceite());
        return entidade;
    }

    static Consentimento paraDominio(ConsentimentoJpaEntidade entidade) {
        return Consentimento.restaurar(
                entidade.getId(),
                entidade.getUsuarioId(),
                entidade.getCriancaId(),
                entidade.getTipo(),
                entidade.getVersaoTermo(),
                entidade.isAceito(),
                entidade.getDataAceite()
        );
    }
}
