package br.com.pueria.pueria.alimentacao.infraestrutura.persistencia;

import br.com.pueria.pueria.alimentacao.dominio.DadosAlimentacao;
import br.com.pueria.pueria.alimentacao.dominio.RegistroAlimentacao;

class RegistroAlimentacaoMapper {

    static RegistroAlimentacao paraDominio(RegistroAlimentacaoJpaEntidade entidade) {
        return RegistroAlimentacao.restaurar(
                entidade.getId(),
                entidade.getCriancaId(),
                dados(entidade),
                entidade.getCriadoEm(),
                entidade.getAtualizadoEm()
        );
    }

    static RegistroAlimentacaoJpaEntidade paraEntidade(RegistroAlimentacao registro) {
        return new RegistroAlimentacaoJpaEntidade(
                registro.getId(),
                registro.getCriancaId(),
                registro.getDataRegistro(),
                registro.getTipoLeite(),
                registro.getEstagioAlimentar(),
                registro.getIdadeInicioAlimentacaoComplementarMeses(),
                registro.getRefeicoesPorDia(),
                registro.getConsomeAgua(),
                registro.getUsaMamadeira(),
                registro.getUsaCopo(),
                registro.getUsaColher(),
                registro.getAutoalimentacao(),
                registro.getTexturaPredominante(),
                registro.getConsomeFrutas(),
                registro.getConsomeLegumesVerduras(),
                registro.getConsomeCereaisTuberculos(),
                registro.getConsomeFeijoesLeguminosas(),
                registro.getConsomeCarnesOvos(),
                registro.getUltraprocessadosFrequentes(),
                registro.getBebidasAdocadas(),
                registro.getAcucarAdicionado(),
                registro.getSalAdicionado(),
                registro.getTelasDuranteRefeicoes(),
                registro.getRefeicoesEmFamilia(),
                registro.getRotinaAlimentarRegular(),
                registro.getSeletividadeAlimentar(),
                registro.getRecusaPersistente(),
                registro.getEngasgosFrequentes(),
                registro.getVomitosRecorrentes(),
                registro.getConstipacao(),
                registro.getDiarreiaRecorrente(),
                registro.getDificuldadeGanhoPesoPercebida(),
                registro.getPreocupacaoFamilia(),
                registro.getObservacao(),
                registro.getCriadoEm(),
                registro.getAtualizadoEm()
        );
    }

    private static DadosAlimentacao dados(RegistroAlimentacaoJpaEntidade entidade) {
        return new DadosAlimentacao(
                entidade.getDataRegistro(),
                entidade.getTipoLeite(),
                entidade.getEstagioAlimentar(),
                entidade.getIdadeInicioAlimentacaoComplementarMeses(),
                entidade.getRefeicoesPorDia(),
                entidade.getConsomeAgua(),
                entidade.getUsaMamadeira(),
                entidade.getUsaCopo(),
                entidade.getUsaColher(),
                entidade.getAutoalimentacao(),
                entidade.getTexturaPredominante(),
                entidade.getConsomeFrutas(),
                entidade.getConsomeLegumesVerduras(),
                entidade.getConsomeCereaisTuberculos(),
                entidade.getConsomeFeijoesLeguminosas(),
                entidade.getConsomeCarnesOvos(),
                entidade.getUltraprocessadosFrequentes(),
                entidade.getBebidasAdocadas(),
                entidade.getAcucarAdicionado(),
                entidade.getSalAdicionado(),
                entidade.getTelasDuranteRefeicoes(),
                entidade.getRefeicoesEmFamilia(),
                entidade.getRotinaAlimentarRegular(),
                entidade.getSeletividadeAlimentar(),
                entidade.getRecusaPersistente(),
                entidade.getEngasgosFrequentes(),
                entidade.getVomitosRecorrentes(),
                entidade.getConstipacao(),
                entidade.getDiarreiaRecorrente(),
                entidade.getDificuldadeGanhoPesoPercebida(),
                entidade.getPreocupacaoFamilia(),
                entidade.getObservacao()
        );
    }
}
