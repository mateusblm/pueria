package br.com.pueria.pueria.consentimentos.dominio;

import br.com.pueria.pueria.comum.excecao.RegraDominioException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConsentimentoTest {

    @Test
    void deveRegistrarConsentimentoAceito() {
        UUID usuarioId = UUID.randomUUID();
        UUID criancaId = UUID.randomUUID();

        Consentimento consentimento = Consentimento.registrarAceite(
                usuarioId,
                criancaId,
                TipoConsentimento.ACOMPANHAMENTO_DESENVOLVIMENTO_INFANTIL,
                "2026.07",
                true
        );

        assertNotNull(consentimento.getId());
        assertEquals(usuarioId, consentimento.getUsuarioId());
        assertEquals(criancaId, consentimento.getCriancaId());
        assertEquals(TipoConsentimento.ACOMPANHAMENTO_DESENVOLVIMENTO_INFANTIL, consentimento.getTipo());
        assertEquals("2026.07", consentimento.getVersaoTermo());
        assertTrue(consentimento.isAceito());
        assertNotNull(consentimento.getDataAceite());
    }

    @Test
    void naoDeveRegistrarConsentimentoNaoAceito() {
        assertThrows(RegraDominioException.class, () -> Consentimento.registrarAceite(
                UUID.randomUUID(),
                UUID.randomUUID(),
                TipoConsentimento.ACOMPANHAMENTO_DESENVOLVIMENTO_INFANTIL,
                "2026.07",
                false
        ));
    }

    @Test
    void naoDeveRegistrarConsentimentoSemVersaoTermo() {
        assertThrows(RegraDominioException.class, () -> Consentimento.registrarAceite(
                UUID.randomUUID(),
                UUID.randomUUID(),
                TipoConsentimento.ACOMPANHAMENTO_DESENVOLVIMENTO_INFANTIL,
                " ",
                true
        ));
    }
}
