package br.com.pueria.pueria.responsaveis.dominio;

import br.com.pueria.pueria.comum.excecao.RegraDominioException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VinculoResponsavelCriancaTest {

    @Test
    void deveCriarVinculoPrincipal() {
        UUID usuarioId = UUID.randomUUID();
        UUID criancaId = UUID.randomUUID();

        VinculoResponsavelCrianca vinculo = VinculoResponsavelCrianca.criarPrincipal(
                usuarioId,
                criancaId,
                Parentesco.MAE
        );

        assertNotNull(vinculo.getId());
        assertEquals(usuarioId, vinculo.getUsuarioId());
        assertEquals(criancaId, vinculo.getCriancaId());
        assertEquals(Parentesco.MAE, vinculo.getParentesco());
        assertTrue(vinculo.isPrincipal());
        assertNotNull(vinculo.getCriadoEm());
    }

    @Test
    void naoDeveCriarVinculoSemResponsavel() {
        assertThrows(RegraDominioException.class, () -> VinculoResponsavelCrianca.criarPrincipal(
                null,
                UUID.randomUUID(),
                Parentesco.MAE
        ));
    }

    @Test
    void naoDeveCriarVinculoSemParentesco() {
        assertThrows(RegraDominioException.class, () -> VinculoResponsavelCrianca.criarPrincipal(
                UUID.randomUUID(),
                UUID.randomUUID(),
                null
        ));
    }
}
