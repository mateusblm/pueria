package br.com.pueria.pueria.telas.infraestrutura.persistencia;

import br.com.pueria.pueria.telas.dominio.DadosTelas;
import br.com.pueria.pueria.telas.dominio.RegistroTelas;

class RegistroTelasMapper {

    static RegistroTelas paraDominio(RegistroTelasJpaEntidade entidade) {
        return RegistroTelas.restaurar(
                entidade.getId(),
                entidade.getCriancaId(),
                dados(entidade),
                entidade.getCriadoEm(),
                entidade.getAtualizadoEm()
        );
    }

    static RegistroTelasJpaEntidade paraEntidade(RegistroTelas registro) {
        return new RegistroTelasJpaEntidade(
                registro.getId(),
                registro.getCriancaId(),
                registro.getDataRegistro(),
                registro.getMinutosDiaSemana(),
                registro.getMinutosFimSemana(),
                registro.getTipoConteudoPredominante(),
                registro.getTelaAoAcordar(),
                registro.getTelaDuranteRefeicoes(),
                registro.getTelaAntesDormir(),
                registro.getTelaParaAcalmar(),
                registro.getTelaEmSegundoPlano(),
                registro.getUsoAcompanhadoAdulto(),
                registro.getConteudoAdultoSupervisionado(),
                registro.getVideochamadaFamilia(),
                registro.getAutoplayAtivo(),
                registro.getNotificacoesAtivas(),
                registro.getDispositivoNoQuarto(),
                registro.getBrincaAoArLivre(),
                registro.getLeituraBrincadeiraSemTela(),
                registro.getPreocupacaoFamilia(),
                registro.getObservacao(),
                registro.getCriadoEm(),
                registro.getAtualizadoEm()
        );
    }

    private static DadosTelas dados(RegistroTelasJpaEntidade entidade) {
        return new DadosTelas(
                entidade.getDataRegistro(),
                entidade.getMinutosDiaSemana(),
                entidade.getMinutosFimSemana(),
                entidade.getTipoConteudoPredominante(),
                entidade.getTelaAoAcordar(),
                entidade.getTelaDuranteRefeicoes(),
                entidade.getTelaAntesDormir(),
                entidade.getTelaParaAcalmar(),
                entidade.getTelaEmSegundoPlano(),
                entidade.getUsoAcompanhadoAdulto(),
                entidade.getConteudoAdultoSupervisionado(),
                entidade.getVideochamadaFamilia(),
                entidade.getAutoplayAtivo(),
                entidade.getNotificacoesAtivas(),
                entidade.getDispositivoNoQuarto(),
                entidade.getBrincaAoArLivre(),
                entidade.getLeituraBrincadeiraSemTela(),
                entidade.getPreocupacaoFamilia(),
                entidade.getObservacao()
        );
    }
}
