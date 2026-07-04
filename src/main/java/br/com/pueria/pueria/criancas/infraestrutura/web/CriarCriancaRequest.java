package br.com.pueria.pueria.criancas.infraestrutura.web;

import br.com.pueria.pueria.criancas.aplicacao.CriarCriancaComando;
import br.com.pueria.pueria.criancas.dominio.Sexo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CriarCriancaRequest(
        @NotBlank(message = "O nome é obrigatório.")
        @Size(max = 150, message = "O nome deve ter no máximo 150 caracteres.")
        String nome,

        @PastOrPresent(message = "A data de nascimento não pode estar no futuro.")
        LocalDate dataNascimento,

        Sexo sexo,
        boolean prematura,
        Integer semanasGestacionais,
        Integer pesoNascimentoGramas
) {

    public CriarCriancaComando paraComando() {
        return new CriarCriancaComando(
                nome,
                dataNascimento,
                sexo,
                prematura,
                semanasGestacionais,
                pesoNascimentoGramas
        );
    }
}
