package br.com.pueria.pueria.desenvolvimento.aplicacao;

import br.com.pueria.pueria.desenvolvimento.dominio.AreaDesenvolvimento;
import br.com.pueria.pueria.desenvolvimento.dominio.PapelClinicoMarcoDesenvolvimento;
import br.com.pueria.pueria.desenvolvimento.dominio.StatusMarcoDesenvolvimento;
import br.com.pueria.pueria.desenvolvimento.dominio.TipoFonteMarcoDesenvolvimento;

import java.time.LocalDateTime;
import java.util.UUID;

public record MarcoDesenvolvimentoResumo(
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
        String observacao,
        LocalDateTime registradoEm
) {
}
