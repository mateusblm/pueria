package br.com.pueria.pueria.desenvolvimento.infraestrutura.web;

import br.com.pueria.pueria.desenvolvimento.aplicacao.MarcoDesenvolvimentoResumo;
import br.com.pueria.pueria.desenvolvimento.dominio.AreaDesenvolvimento;
import br.com.pueria.pueria.desenvolvimento.dominio.PapelClinicoMarcoDesenvolvimento;
import br.com.pueria.pueria.desenvolvimento.dominio.ModalidadeRegistroMarcoDesenvolvimento;
import br.com.pueria.pueria.desenvolvimento.dominio.StatusMarcoDesenvolvimento;
import br.com.pueria.pueria.desenvolvimento.dominio.TipoFonteMarcoDesenvolvimento;

import java.time.LocalDateTime;
import java.util.UUID;

public record MarcoDesenvolvimentoResponse(
        UUID id,
        int idadeMeses,
        AreaDesenvolvimento area,
        String descricao,
        String fonte,
        TipoFonteMarcoDesenvolvimento tipoFonte,
        String versaoCatalogo,
        PapelClinicoMarcoDesenvolvimento papelClinico,
        boolean altaRelevanciaVigilancia,
        StatusMarcoDesenvolvimento status,
        ModalidadeRegistroMarcoDesenvolvimento modalidade,
        String observacao,
        LocalDateTime registradoEm
) {
    public static MarcoDesenvolvimentoResponse de(MarcoDesenvolvimentoResumo resumo) {
        return new MarcoDesenvolvimentoResponse(
                resumo.id(),
                resumo.idadeMeses(),
                resumo.area(),
                resumo.descricao(),
                resumo.fonte(),
                resumo.tipoFonte(),
                resumo.versaoCatalogo(),
                resumo.papelClinico(),
                resumo.altaRelevanciaVigilancia(),
                resumo.status(),
                resumo.modalidade(),
                resumo.observacao(),
                resumo.registradoEm()
        );
    }
}
