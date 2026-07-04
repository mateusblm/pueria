package br.com.pueria.pueria.criancas.infraestrutura.web;

import br.com.pueria.pueria.criancas.aplicacao.AtualizarCriancaComando;
import br.com.pueria.pueria.criancas.dominio.Sexo;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

public record AtualizarCriancaRequest(
        @NotBlank(message = "O nome é obrigatório.")
        @Size(max = 150, message = "O nome deve ter no máximo 150 caracteres.")
        String nome,

        @NotNull(message = "A data de nascimento é obrigatória.")
        @PastOrPresent(message = "A data de nascimento não pode estar no futuro.")
        LocalDate dataNascimento,

        Sexo sexo,

        boolean prematura,

        @NotNull(message = "As semanas gestacionais são obrigatórias.")
        @Min(value = 22, message = "As semanas gestacionais devem estar entre 22 e 42.")
        @Max(value = 42, message = "As semanas gestacionais devem estar entre 22 e 42.")
        Integer semanasGestacionais,

        @NotNull(message = "O peso ao nascer é obrigatório.")
        @Min(value = 300, message = "O peso de nascimento informado está fora do limite operacional permitido.")
        @Max(value = 7000, message = "O peso de nascimento informado está fora do limite operacional permitido.")
        Integer pesoNascimentoGramas
) {

    public AtualizarCriancaComando paraComando(UUID id, String emailResponsavel) {
        return new AtualizarCriancaComando(
                id,
                emailResponsavel,
                nome,
                dataNascimento,
                sexo,
                prematura,
                semanasGestacionais,
                pesoNascimentoGramas
        );
    }
}
