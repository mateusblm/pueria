package br.com.pueria.pueria.crescimento.infraestrutura.persistencia;

import br.com.pueria.pueria.crescimento.dominio.MedidaCrescimento;

class MedidaCrescimentoMapper {

    static MedidaCrescimento paraDominio(MedidaCrescimentoJpaEntidade entidade) {
        return MedidaCrescimento.restaurar(
                entidade.getId(),
                entidade.getCriancaId(),
                entidade.getDataMedicao(),
                entidade.getPesoKg(),
                entidade.getComprimentoCm(),
                entidade.getPerimetroCefalicoCm(),
                entidade.getOrigem(),
                entidade.getResponsavelMedicao(),
                entidade.getObservacao(),
                entidade.getCriadoEm(),
                entidade.getAtualizadoEm()
        );
    }

    static MedidaCrescimentoJpaEntidade paraEntidade(MedidaCrescimento medida) {
        return new MedidaCrescimentoJpaEntidade(
                medida.getId(),
                medida.getCriancaId(),
                medida.getDataMedicao(),
                medida.getPesoKg(),
                medida.getComprimentoCm(),
                medida.getPerimetroCefalicoCm(),
                medida.getOrigem(),
                medida.getResponsavelMedicao(),
                medida.getObservacao(),
                medida.getCriadoEm(),
                medida.getAtualizadoEm()
        );
    }
}
