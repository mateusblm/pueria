package br.com.pueria.pueria.criancas.infraestrutura.web;

import br.com.pueria.pueria.criancas.aplicacao.CriarCriancaComando;
import br.com.pueria.pueria.criancas.dominio.Sexo;
import br.com.pueria.pueria.responsaveis.dominio.Parentesco;
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
        Integer semanasGestacionais,
        Integer pesoNascimentoGramas,

        @NotNull(message = "O parentesco é obrigatório.")
        Parentesco parentesco,

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
