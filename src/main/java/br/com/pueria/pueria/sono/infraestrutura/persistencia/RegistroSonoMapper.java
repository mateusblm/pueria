package br.com.pueria.pueria.sono.infraestrutura.persistencia;

import br.com.pueria.pueria.sono.dominio.DadosSono;
import br.com.pueria.pueria.sono.dominio.RegistroSono;

class RegistroSonoMapper {

    static RegistroSono paraDominio(RegistroSonoJpaEntidade entidade) {
        return RegistroSono.restaurar(
                entidade.getId(),
                entidade.getCriancaId(),
                dados(entidade),
                entidade.getCriadoEm(),
                entidade.getAtualizadoEm()
        );
    }

    static RegistroSonoJpaEntidade paraEntidade(RegistroSono registro) {
        return new RegistroSonoJpaEntidade(
                registro.getId(),
                registro.getCriancaId(),
                registro.getDataRegistro(),
                registro.getHorarioDormiu(),
                registro.getHorarioAcordou(),
                registro.getQuantidadeCochilos(),
                registro.getMinutosCochilos(),
                registro.getDespertaresNoturnos(),
                registro.getDificuldadeIniciarSono(),
                registro.getRotinaSonoConsistente(),
                registro.getTelasAntesDormir(),
                registro.getSuperficieSono(),
                registro.getAmbienteSono(),
                registro.getTiposDespertarNoturno(),
                registro.getRoncosFrequentes(),
                registro.getPausasRespiratoriasPercebidas(),
                registro.getSonoAgitado(),
                registro.getRangerDentesDuranteSono(),
                registro.getAcordaBemDisposto(),
                registro.getSonolenciaDiurna(),
                registro.getIrritabilidadeCansaco(),
                registro.getDificilDeSerAcordado(),
                registro.getMalHumorado(),
                registro.getIrritado(),
                registro.getPreocupacaoFamilia(),
                registro.getObservacao(),
                registro.getCriadoEm(),
                registro.getAtualizadoEm()
        );
    }

    private static DadosSono dados(RegistroSonoJpaEntidade entidade) {
        return new DadosSono(
                entidade.getDataRegistro(),
                entidade.getHorarioDormiu(),
                entidade.getHorarioAcordou(),
                entidade.getQuantidadeCochilos(),
                entidade.getMinutosCochilos(),
                entidade.getDespertaresNoturnos(),
                entidade.getDificuldadeIniciarSono(),
                entidade.getRotinaSonoConsistente(),
                entidade.getTelasAntesDormir(),
                entidade.getSuperficieSono(),
                entidade.getAmbienteSono(),
                entidade.getTiposDespertarNoturno(),
                entidade.getRoncosFrequentes(),
                entidade.getPausasRespiratoriasPercebidas(),
                entidade.getSonoAgitado(),
                entidade.getRangerDentesDuranteSono(),
                entidade.getAcordaBemDisposto(),
                entidade.getSonolenciaDiurna(),
                entidade.getIrritabilidadeCansaco(),
                entidade.getDificilDeSerAcordado(),
                entidade.getMalHumorado(),
                entidade.getIrritado(),
                entidade.getPreocupacaoFamilia(),
                entidade.getObservacao()
        );
    }
}
