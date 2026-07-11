package br.com.pueria.pueria.crescimento.aplicacao;

import br.com.pueria.pueria.crescimento.dominio.OrigemMedidaCrescimento;
import br.com.pueria.pueria.crescimento.dominio.ResponsavelMedicaoCrescimento;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record AtualizarMedidaCrescimentoComando(
        UUID criancaId,
        UUID medidaId,
        String emailResponsavel,
        LocalDate dataMedicao,
        BigDecimal pesoKg,
        BigDecimal comprimentoCm,
        BigDecimal perimetroCefalicoCm,
        OrigemMedidaCrescimento origem,
        ResponsavelMedicaoCrescimento responsavelMedicao,
        String observacao
) {
    public AtualizarMedidaCrescimentoComando(UUID criancaId, UUID medidaId, String emailResponsavel, LocalDate dataMedicao, BigDecimal pesoKg, BigDecimal comprimentoCm, BigDecimal perimetroCefalicoCm, OrigemMedidaCrescimento origem, String observacao) {
        this(criancaId, medidaId, emailResponsavel, dataMedicao, pesoKg, comprimentoCm, perimetroCefalicoCm, origem, ResponsavelMedicaoCrescimento.NAO_INFORMADO, observacao);
    }
}
