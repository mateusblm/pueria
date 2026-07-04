package br.com.pueria.pueria.criancas.infraestrutura.web;

import br.com.pueria.pueria.criancas.aplicacao.CriarCriancaComando;
import br.com.pueria.pueria.criancas.dominio.Sexo;
import br.com.pueria.pueria.responsaveis.dominio.Parentesco;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CriarCriancaRequest(
        @NotBlank(message = "O nome é obrigatório.")
        @Size(max = 150, message = "O nome deve ter no máximo 150 caracteres.")
        String nome,

        @NotNull(message = "A data de nascimento é obrigatória.")
        @PastOrPresent(message = "A data de nascimento não pode estar no futuro.")
        LocalDate dataNascimento,

        Sexo sexo,

        boolean prematura,

        @NotNull(message = "As semanas gestacionais são obrigatórias.")
        @Min(value = 22, message = "As semanas gestacionais devem ser no mínimo 22.")
        @Max(value = 42, message = "As semanas gestacionais devem ser no máximo 42.")
        Integer semanasGestacionais,

        @NotNull(message = "O peso de nascimento é obrigatório.")
        @Min(value = 300, message = "O peso de nascimento deve ser no mínimo 300 gramas.")
        @Max(value = 7000, message = "O peso de nascimento deve ser no máximo 7000 gramas.")
        Integer pesoNascimentoGramas,

        @NotNull(message = "O parentesco é obrigatório.")
        Parentesco parentesco,

        @AssertTrue(message = "O consentimento precisa estar aceito para cadastrar a criança.")
        boolean aceiteConsentimento,

        @NotBlank(message = "A versão do termo de consentimento é obrigatória.")
        @Size(max = 30, message = "A versão do termo de consentimento deve ter no máximo 30 caracteres.")
        String versaoTermoConsentimento
) {
    public CriarCriancaComando paraComando(String emailResponsavel) {
        return new CriarCriancaComando(
                emailResponsavel,
                nome,
                dataNascimento,
                sexo,
                prematura,
                semanasGestacionais,
                pesoNascimentoGramas,
                parentesco,
                aceiteConsentimento,
                versaoTermoConsentimento
        );
    }
}
