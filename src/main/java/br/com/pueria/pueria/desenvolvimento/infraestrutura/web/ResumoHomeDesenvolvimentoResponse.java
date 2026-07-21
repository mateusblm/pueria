package br.com.pueria.pueria.desenvolvimento.infraestrutura.web;

import br.com.pueria.pueria.desenvolvimento.aplicacao.MarcoDesenvolvimentoResumo;
import br.com.pueria.pueria.desenvolvimento.dominio.RelatoDesenvolvimento;
import br.com.pueria.pueria.desenvolvimento.dominio.StatusMarcoDesenvolvimento;
import br.com.pueria.pueria.desenvolvimento.dominio.TipoRelatoDesenvolvimento;

import java.util.List;

/** Contrato enxuto para a home decidir qual estado visual apresentar. */
public record ResumoHomeDesenvolvimentoResponse(
        Estado estado,
        int total,
        int respondidos,
        int pontosAtencao,
        boolean temPerdaHabilidade
) {
    public enum Estado { INICIAL, ATENCAO, TRANQUILO }

    public static ResumoHomeDesenvolvimentoResponse de(
            List<MarcoDesenvolvimentoResumo> marcos,
            List<RelatoDesenvolvimento> relatos
    ) {
        int respondidos = (int) marcos.stream()
                .filter(marco -> marco.status() != StatusMarcoDesenvolvimento.NAO_AVALIADO)
                .count();
        int pontosAtencao = (int) marcos.stream()
                .filter(marco -> marco.status() == StatusMarcoDesenvolvimento.NAO_AVALIADO
                        || marco.status() == StatusMarcoDesenvolvimento.AINDA_NAO_OBSERVADO
                        || marco.status() == StatusMarcoDesenvolvimento.NAO_TENHO_CERTEZA
                        || marco.status() == StatusMarcoDesenvolvimento.NAO_LEMBRO)
                .count();
        boolean temPerdaHabilidade = relatos.stream()
                .anyMatch(relato -> relato.getTipo() == TipoRelatoDesenvolvimento.PERDA_HABILIDADE);
        Estado estado = respondidos == 0
                ? Estado.INICIAL
                : pontosAtencao > 0 || temPerdaHabilidade ? Estado.ATENCAO : Estado.TRANQUILO;
        return new ResumoHomeDesenvolvimentoResponse(estado, marcos.size(), respondidos, pontosAtencao, temPerdaHabilidade);
    }
}
