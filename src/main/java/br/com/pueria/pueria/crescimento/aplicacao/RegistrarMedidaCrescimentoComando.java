package br.com.pueria.pueria.crescimento.aplicacao;

import br.com.pueria.pueria.crescimento.dominio.OrigemMedidaCrescimento;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record RegistrarMedidaCrescimentoComando(
        UUID criancaId,
        String emailResponsavel,
        LocalDate dataMedicao,
        BigDecimal pesoKg,
        BigDecimal comprimentoCm,
        BigDecimal perimetroCefalicoCm,
        OrigemMedidaCrescimento origem,
        String observacao
) {}
