package br.com.pueria.pueria.saude.infraestrutura.web;

import br.com.pueria.pueria.saude.dominio.DadosRegistroSaude;
import br.com.pueria.pueria.saude.dominio.TipoRegistroSaude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record RegistroSaudeRequest(
        @NotNull TipoRegistroSaude tipo,
        @NotNull LocalDate dataRegistro,
        @NotBlank @Size(max = 4000) String descricao
) {
    DadosRegistroSaude paraDominio() { return new DadosRegistroSaude(tipo, dataRegistro, descricao); }
}
