package br.com.pueria.pueria.transitointestinal.infraestrutura.persistencia;

import br.com.pueria.pueria.transitointestinal.dominio.DadosTransitoIntestinal;
import br.com.pueria.pueria.transitointestinal.dominio.RegistroTransitoIntestinal;

class RegistroTransitoIntestinalMapper {

    static RegistroTransitoIntestinal paraDominio(RegistroTransitoIntestinalJpaEntidade entidade) {
        return RegistroTransitoIntestinal.restaurar(
                entidade.getId(),
                entidade.getCriancaId(),
                dados(entidade),
                entidade.getCriadoEm(),
                entidade.getAtualizadoEm()
        );
    }

    static RegistroTransitoIntestinalJpaEntidade paraEntidade(RegistroTransitoIntestinal registro) {
        return new RegistroTransitoIntestinalJpaEntidade(
                registro.getId(),
                registro.getCriancaId(),
                registro.getDataRegistro(),
                registro.getTipoFezes(),
                registro.getEvacuacoesPorDia(),
                registro.getIntervaloDiureseHoras(),
                registro.getCorUrina(),
                registro.getAspectoUrina(),
                registro.getCheiroUrina(),
                registro.getDiureseSemAlteracoes(),
                registro.getFacilidadeLimpeza(),
                registro.getMuco(),
                registro.getRestosAlimentares(),
                registro.getRaiasSangue(),
                registro.getConstipacao(),
                registro.getDiarreia(),
                registro.getDorEvacuar(),
                registro.getEscapeFecal(),
                registro.getAssaduraFrequente(),
                registro.getAssaduraVermelhidao(),
                registro.getAssaduraPontosVermelhos(),
                registro.getPreocupacaoFamilia(),
                registro.getObservacao(),
                registro.getCriadoEm(),
                registro.getAtualizadoEm()
        );
    }

    private static DadosTransitoIntestinal dados(RegistroTransitoIntestinalJpaEntidade entidade) {
        return new DadosTransitoIntestinal(
                entidade.getDataRegistro(),
                entidade.getTipoFezes(),
                entidade.getEvacuacoesPorDia(),
                entidade.getIntervaloDiureseHoras(),
                entidade.getCorUrina(),
                entidade.getAspectoUrina(),
                entidade.getCheiroUrina(),
                entidade.getDiureseSemAlteracoes(),
                entidade.getFacilidadeLimpeza(),
                entidade.getMuco(),
                entidade.getRestosAlimentares(),
                entidade.getRaiasSangue(),
                entidade.getConstipacao(),
                entidade.getDiarreia(),
                entidade.getDorEvacuar(),
                entidade.getEscapeFecal(),
                entidade.getAssaduraFrequente(),
                entidade.getAssaduraVermelhidao(),
                entidade.getAssaduraPontosVermelhos(),
                entidade.getPreocupacaoFamilia(),
                entidade.getObservacao()
        );
    }
}
