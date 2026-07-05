package br.com.pueria.pueria.crescimento.infraestrutura.web;

import br.com.pueria.pueria.crescimento.aplicacao.AtualizarMedidaCrescimentoComando;
import br.com.pueria.pueria.crescimento.aplicacao.RegistrarMedidaCrescimentoComando;
import br.com.pueria.pueria.crescimento.dominio.OrigemMedidaCrescimento;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record MedidaCrescimentoRequest(
        @PastOrPresent(message = "A data da medição não pode estar no futuro.")
        LocalDate dataMedicao,

        @DecimalMin(value = "0.3", message = "O peso informado está fora do limite esperado.")
        @DecimalMax(value = "80.0", message = "O peso informado está fora do limite esperado.")
        BigDecimal pesoKg,

        @DecimalMin(value = "20.0", message = "O comprimento/estatura informado está fora do limite esperado.")
        @DecimalMax(value = "140.0", message = "O comprimento/estatura informado está fora do limite esperado.")
        BigDecimal comprimentoCm,

        @DecimalMin(value = "20.0", message = "O perímetro cefálico informado está fora do limite esperado.")
        @DecimalMax(value = "65.0", message = "O perímetro cefálico informado está fora do limite esperado.")
        BigDecimal perimetroCefalicoCm,

        OrigemMedidaCrescimento origem,

        @Size(max = 500, message = "A observação deve ter no máximo 500 caracteres.")
        String observacao
) {
    RegistrarMedidaCrescimentoComando paraRegistrar(UUID criancaId, String emailResponsavel) {
        return new RegistrarMedidaCrescimentoComando(criancaId, emailResponsavel, dataMedicao, pesoKg, comprimentoCm, perimetroCefalicoCm, origem, observacao);
    }

    AtualizarMedidaCrescimentoComando paraAtualizar(UUID criancaId, UUID medidaId, String emailResponsavel) {
        return new AtualizarMedidaCrescimentoComando(criancaId, medidaId, emailResponsavel, dataMedicao, pesoKg, comprimentoCm, perimetroCefalicoCm, origem, observacao);
    }
}
