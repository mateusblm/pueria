package br.com.pueria.pueria.desenvolvimento.dominio;

import br.com.pueria.pueria.comum.excecao.RegraDominioException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MarcoDesenvolvimentoTest {

    @Test
    void restauraMarcoComMetadadosClinicosVersionados() {
        MarcoDesenvolvimento marco = MarcoDesenvolvimento.restaurar(
                UUID.randomUUID(),
                9,
                AreaDesenvolvimento.SOCIAL_EMOCIONAL,
                "Reage quando é chamada pelo nome.",
                "CDC Learn the Signs. Act Early (2022)",
                TipoFonteMarcoDesenvolvimento.CDC_2022,
                "NEURO_V2_2026_07",
                PapelClinicoMarcoDesenvolvimento.ALTA_RELEVANCIA,
                true,
                true
        );

        assertEquals(TipoFonteMarcoDesenvolvimento.CDC_2022, marco.getTipoFonte());
        assertEquals("NEURO_V2_2026_07", marco.getVersaoCatalogo());
        assertEquals(PapelClinicoMarcoDesenvolvimento.ALTA_RELEVANCIA, marco.getPapelClinico());
        assertTrue(marco.isAltaRelevanciaVigilancia());
    }

    @Test
    void rejeitaMarcoSemVersaoDeCatalogo() {
        assertThrows(RegraDominioException.class, () -> MarcoDesenvolvimento.restaurar(
                UUID.randomUUID(),
                6,
                AreaDesenvolvimento.MOTOR,
                "Senta sem apoio.",
                "OMS, Motor Development Study (2006)",
                TipoFonteMarcoDesenvolvimento.OMS,
                " ",
                PapelClinicoMarcoDesenvolvimento.ATENCAO_PERSISTENTE,
                false,
                true
        ));
    }
}
