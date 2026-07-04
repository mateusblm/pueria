package br.com.pueria.pueria.desenvolvimento.infraestrutura.web;

import br.com.pueria.pueria.desenvolvimento.aplicacao.MarcoDesenvolvimentoResumo;
import br.com.pueria.pueria.desenvolvimento.dominio.AreaDesenvolvimento;
import br.com.pueria.pueria.desenvolvimento.dominio.StatusMarcoDesenvolvimento;

import java.time.LocalDateTime;
import java.util.UUID;

public record MarcoDesenvolvimentoResponse(
        UUID id,
        int idadeMeses,
        AreaDesenvolvimento area,
        String descricao,
        String fonte,
        StatusMarcoDesenvolvimento status,
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
                resumo.status(),
                resumo.observacao(),
                resumo.registradoEm()
        );
    }
}
