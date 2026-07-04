package br.com.pueria.pueria.desenvolvimento.infraestrutura.web;

import br.com.pueria.pueria.desenvolvimento.aplicacao.RegistrarMarcoDesenvolvimentoComando;
import br.com.pueria.pueria.desenvolvimento.dominio.StatusMarcoDesenvolvimento;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record RegistrarMarcoDesenvolvimentoRequest(
        @NotNull(message = "O status do marco é obrigatório.")
        StatusMarcoDesenvolvimento status,

        @Size(max = 500, message = "A observação deve ter no máximo 500 caracteres.")
        String observacao
) {
    public RegistrarMarcoDesenvolvimentoComando paraComando(UUID criancaId, UUID marcoId, String emailResponsavel) {
        return new RegistrarMarcoDesenvolvimentoComando(criancaId, marcoId, emailResponsavel, status, observacao);
    }
}
