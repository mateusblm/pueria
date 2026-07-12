package br.com.pueria.pueria.desenvolvimento.infraestrutura.web;

import br.com.pueria.pueria.desenvolvimento.dominio.TipoRelatoDesenvolvimento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegistrarRelatoDesenvolvimentoRequest(
        @NotNull TipoRelatoDesenvolvimento tipo,
        @NotBlank @Size(max = 500) String descricao
) { }
