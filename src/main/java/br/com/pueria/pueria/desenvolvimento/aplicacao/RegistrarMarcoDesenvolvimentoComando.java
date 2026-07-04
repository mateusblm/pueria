package br.com.pueria.pueria.desenvolvimento.aplicacao;

import br.com.pueria.pueria.desenvolvimento.dominio.StatusMarcoDesenvolvimento;

import java.util.UUID;

public record RegistrarMarcoDesenvolvimentoComando(
        UUID criancaId,
        UUID marcoId,
        String emailResponsavel,
        StatusMarcoDesenvolvimento status,
        String observacao
) {
}
