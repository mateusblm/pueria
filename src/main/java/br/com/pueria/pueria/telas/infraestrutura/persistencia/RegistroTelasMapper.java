package br.com.pueria.pueria.telas.infraestrutura.persistencia;

import br.com.pueria.pueria.telas.dominio.DadosTelas;
import br.com.pueria.pueria.telas.dominio.RegistroTelas;
import br.com.pueria.pueria.telas.dominio.ContextoUsoTela;

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
                registro.getContextosUso().stream().map(contexto -> new ContextoUsoTelaJpa(contexto.dispositivo(), contexto.conteudo())).toList(),
                registro.getTelaAoAcordar(),
                registro.getTelaDuranteRefeicoes(),
                registro.getTelaAntesDormir(),
                registro.getTelaParaAcalmar(),
                registro.getTelaEmSegundoPlano(),
                registro.getUsoAcompanhadoAdulto(),
                registro.getConteudoAdultoSupervisionado(),
                registro.getCriancaEscolheConteudoLivremente(),
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
                entidade.getContextosUso() == null ? java.util.List.of() : entidade.getContextosUso().stream().map(contexto -> new ContextoUsoTela(contexto.getDispositivo(), contexto.getConteudo())).toList(),
                entidade.getTelaAoAcordar(),
                entidade.getTelaDuranteRefeicoes(),
                entidade.getTelaAntesDormir(),
                entidade.getTelaParaAcalmar(),
                entidade.getTelaEmSegundoPlano(),
                entidade.getUsoAcompanhadoAdulto(),
                entidade.getConteudoAdultoSupervisionado(),
                entidade.getCriancaEscolheConteudoLivremente(),
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
