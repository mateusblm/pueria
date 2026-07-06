package br.com.pueria.pueria.criancas.infraestrutura.web;

import br.com.pueria.pueria.criancas.aplicacao.AtualizarCriancaComando;
import br.com.pueria.pueria.criancas.dominio.Sexo;
import br.com.pueria.pueria.criancas.dominio.TipoParto;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
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

        @NotNull(message = "Os dias gestacionais são obrigatórios.")
        @Min(value = 0, message = "Os dias gestacionais devem estar entre 0 e 6.")
        @Max(value = 6, message = "Os dias gestacionais devem estar entre 0 e 6.")
        Integer diasGestacionais,

        @NotNull(message = "O tipo de parto é obrigatório.")
        TipoParto tipoParto,

        @NotNull(message = "O peso ao nascer é obrigatório.")
        @Min(value = 300, message = "O peso de nascimento informado está fora do limite operacional permitido.")
        @Max(value = 7000, message = "O peso de nascimento informado está fora do limite operacional permitido.")
        Integer pesoNascimentoGramas,

        @NotNull(message = "O comprimento ao nascer é obrigatório.")
        @DecimalMin(value = "20.0", message = "O comprimento ao nascer informado está fora do limite operacional permitido.")
        @DecimalMax(value = "70.0", message = "O comprimento ao nascer informado está fora do limite operacional permitido.")
        BigDecimal comprimentoNascimentoCm,

        @NotNull(message = "O perímetro cefálico ao nascer é obrigatório.")
        @DecimalMin(value = "20.0", message = "O perímetro cefálico ao nascer informado está fora do limite operacional permitido.")
        @DecimalMax(value = "50.0", message = "O perímetro cefálico ao nascer informado está fora do limite operacional permitido.")
        BigDecimal perimetroCefalicoNascimentoCm,

        @Min(value = 0, message = "O Apgar deve estar entre 0 e 10.")
        @Max(value = 10, message = "O Apgar deve estar entre 0 e 10.")
        Integer apgarUmMinuto,

        @Min(value = 0, message = "O Apgar deve estar entre 0 e 10.")
        @Max(value = 10, message = "O Apgar deve estar entre 0 e 10.")
        Integer apgarCincoMinutos,

        boolean utiNeonatal,
        boolean reanimacaoNeonatal,
        boolean ictericiaNeonatal,
        boolean dificuldadeRespiratoria,
        boolean dificuldadeAmamentacao,

        @Size(max = 1000, message = "As observações do nascimento devem ter no máximo 1000 caracteres.")
        String observacoesNascimento
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
                diasGestacionais,
                tipoParto,
                pesoNascimentoGramas,
                comprimentoNascimentoCm,
                perimetroCefalicoNascimentoCm,
                apgarUmMinuto,
                apgarCincoMinutos,
                utiNeonatal,
                reanimacaoNeonatal,
                ictericiaNeonatal,
                dificuldadeRespiratoria,
                dificuldadeAmamentacao,
                observacoesNascimento
        );
    }
}
