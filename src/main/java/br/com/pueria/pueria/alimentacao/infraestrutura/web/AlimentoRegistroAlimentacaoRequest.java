package br.com.pueria.pueria.alimentacao.infraestrutura.web;

import br.com.pueria.pueria.alimentacao.dominio.AlimentoRegistroAlimentacao;
import br.com.pueria.pueria.alimentacao.dominio.GrupoAlimento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AlimentoRegistroAlimentacaoRequest(
        @NotBlank @Size(max = 80) String codigo,
        @NotBlank @Size(max = 120) String nome,
        @NotNull GrupoAlimento grupo
) {
    AlimentoRegistroAlimentacao paraDominio() {
        return new AlimentoRegistroAlimentacao(codigo, nome, grupo);
    }
}
