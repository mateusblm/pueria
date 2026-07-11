package br.com.pueria.pueria.crescimento.infraestrutura.web;

import br.com.pueria.pueria.crescimento.dominio.MedidaCrescimento;
import br.com.pueria.pueria.crescimento.dominio.OrigemMedidaCrescimento;
import br.com.pueria.pueria.crescimento.dominio.ResponsavelMedicaoCrescimento;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record MedidaCrescimentoResponse(
        UUID id,
        UUID criancaId,
        LocalDate dataMedicao,
        BigDecimal pesoKg,
        BigDecimal comprimentoCm,
        BigDecimal perimetroCefalicoCm,
        OrigemMedidaCrescimento origem,
        ResponsavelMedicaoCrescimento responsavelMedicao,
        String observacao,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm
) {
    static MedidaCrescimentoResponse de(MedidaCrescimento medida) {
        return new MedidaCrescimentoResponse(
                medida.getId(),
                medida.getCriancaId(),
                medida.getDataMedicao(),
                medida.getPesoKg(),
                medida.getComprimentoCm(),
                medida.getPerimetroCefalicoCm(),
                medida.getOrigem(),
                medida.getResponsavelMedicao(),
                medida.getObservacao(),
                medida.getCriadoEm(),
                medida.getAtualizadoEm()
        );
    }
}
