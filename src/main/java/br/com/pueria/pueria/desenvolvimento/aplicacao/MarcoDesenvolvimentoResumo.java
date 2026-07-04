package br.com.pueria.pueria.desenvolvimento.aplicacao;

import br.com.pueria.pueria.desenvolvimento.dominio.AreaDesenvolvimento;
import br.com.pueria.pueria.desenvolvimento.dominio.StatusMarcoDesenvolvimento;

import java.time.LocalDateTime;
import java.util.UUID;

public record MarcoDesenvolvimentoResumo(
        UUID id,
        int idadeMeses,
        AreaDesenvolvimento area,
        String descricao,
        String fonte,
        StatusMarcoDesenvolvimento status,
        String observacao,
        LocalDateTime registradoEm
) {
}
