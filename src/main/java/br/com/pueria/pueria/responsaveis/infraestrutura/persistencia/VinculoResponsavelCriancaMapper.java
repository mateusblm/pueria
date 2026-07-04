package br.com.pueria.pueria.responsaveis.infraestrutura.persistencia;

import br.com.pueria.pueria.responsaveis.dominio.VinculoResponsavelCrianca;

final class VinculoResponsavelCriancaMapper {

    private VinculoResponsavelCriancaMapper() {
    }

    static VinculoResponsavelCriancaJpaEntidade paraEntidade(VinculoResponsavelCrianca vinculo) {
        VinculoResponsavelCriancaJpaEntidade entidade = new VinculoResponsavelCriancaJpaEntidade();
        entidade.setId(vinculo.getId());
        entidade.setUsuarioId(vinculo.getUsuarioId());
        entidade.setCriancaId(vinculo.getCriancaId());
        entidade.setParentesco(vinculo.getParentesco());
        entidade.setPrincipal(vinculo.isPrincipal());
        entidade.setCriadoEm(vinculo.getCriadoEm());
        return entidade;
    }

    static VinculoResponsavelCrianca paraDominio(VinculoResponsavelCriancaJpaEntidade entidade) {
        return VinculoResponsavelCrianca.restaurar(
                entidade.getId(),
                entidade.getUsuarioId(),
                entidade.getCriancaId(),
                entidade.getParentesco(),
                entidade.isPrincipal(),
                entidade.getCriadoEm()
        );
    }
}
