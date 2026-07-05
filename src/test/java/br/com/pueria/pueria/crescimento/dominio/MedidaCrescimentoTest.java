package br.com.pueria.pueria.crescimento.dominio;

import br.com.pueria.pueria.comum.excecao.RegraDominioException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MedidaCrescimentoTest {

    @Test
    void deveRegistrarMedidaComDadosValidos() {
        UUID criancaId = UUID.randomUUID();

        MedidaCrescimento medida = MedidaCrescimento.registrar(
                criancaId,
                LocalDate.now(),
                new BigDecimal("7.35"),
                new BigDecimal("68.5"),
                new BigDecimal("43.2"),
                OrigemMedidaCrescimento.CONSULTA,
                "  consulta de rotina  "
        );

        assertNotNull(medida.getId());
        assertEquals(criancaId, medida.getCriancaId());
        assertEquals(new BigDecimal("7.35"), medida.getPesoKg());
        assertEquals(new BigDecimal("68.5"), medida.getComprimentoCm());
        assertEquals(new BigDecimal("43.2"), medida.getPerimetroCefalicoCm());
        assertEquals(OrigemMedidaCrescimento.CONSULTA, medida.getOrigem());
        assertEquals("consulta de rotina", medida.getObservacao());
        assertNull(medida.getAtualizadoEm());
    }

    @Test
    void deveAssumirOrigemConsultaQuandoNaoInformada() {
        MedidaCrescimento medida = MedidaCrescimento.registrar(
                UUID.randomUUID(),
                LocalDate.now(),
                new BigDecimal("7.35"),
                null,
                null,
                null,
                null
        );

        assertEquals(OrigemMedidaCrescimento.CONSULTA, medida.getOrigem());
    }

    @Test
    void naoDeveRegistrarSemNenhumaMedida() {
        assertThrows(RegraDominioException.class, () -> MedidaCrescimento.registrar(
                UUID.randomUUID(),
                LocalDate.now(),
                null,
                null,
                null,
                OrigemMedidaCrescimento.CASA,
                null
        ));
    }

    @Test
    void naoDeveRegistrarComDataFutura() {
        assertThrows(RegraDominioException.class, () -> MedidaCrescimento.registrar(
                UUID.randomUUID(),
                LocalDate.now().plusDays(1),
                new BigDecimal("7.35"),
                null,
                null,
                OrigemMedidaCrescimento.CASA,
                null
        ));
    }

    @Test
    void naoDeveRegistrarPesoForaDoLimiteOperacional() {
        assertThrows(RegraDominioException.class, () -> MedidaCrescimento.registrar(
                UUID.randomUUID(),
                LocalDate.now(),
                new BigDecimal("0.2"),
                null,
                null,
                OrigemMedidaCrescimento.CASA,
                null
        ));
    }

    @Test
    void deveAtualizarMedidaMantendoIdentificadorECriacao() {
        MedidaCrescimento medida = MedidaCrescimento.registrar(
                UUID.randomUUID(),
                LocalDate.now().minusDays(10),
                new BigDecimal("7.35"),
                null,
                null,
                OrigemMedidaCrescimento.CASA,
                null
        );

        MedidaCrescimento atualizada = medida.atualizar(
                LocalDate.now(),
                new BigDecimal("7.70"),
                new BigDecimal("69.2"),
                null,
                OrigemMedidaCrescimento.CONSULTA,
                "medida conferida"
        );

        assertEquals(medida.getId(), atualizada.getId());
        assertEquals(medida.getCriadoEm(), atualizada.getCriadoEm());
        assertEquals(new BigDecimal("7.70"), atualizada.getPesoKg());
        assertEquals(new BigDecimal("69.2"), atualizada.getComprimentoCm());
        assertEquals(OrigemMedidaCrescimento.CONSULTA, atualizada.getOrigem());
        assertNotNull(atualizada.getAtualizadoEm());
    }
}
