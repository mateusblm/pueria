package br.com.pueria.pueria.desenvolvimento.dominio;

import br.com.pueria.pueria.comum.excecao.RegraDominioException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RelatoDesenvolvimentoTest {

    @Test
    void registraPerdaDeHabilidadeComDescricaoNormalizada() {
        RelatoDesenvolvimento relato = RelatoDesenvolvimento.registrar(
                UUID.randomUUID(), TipoRelatoDesenvolvimento.PERDA_HABILIDADE, "  Parou de repetir um gesto.  ");

        assertEquals(TipoRelatoDesenvolvimento.PERDA_HABILIDADE, relato.getTipo());
        assertEquals("Parou de repetir um gesto.", relato.getDescricao());
    }

    @Test
    void exigeContextoNaoVazioEDeTamanhoSeguro() {
        assertThrows(RegraDominioException.class, () -> RelatoDesenvolvimento.registrar(
                UUID.randomUUID(), TipoRelatoDesenvolvimento.PREOCUPACAO_FAMILIA, "   "));
        assertThrows(RegraDominioException.class, () -> RelatoDesenvolvimento.registrar(
                UUID.randomUUID(), TipoRelatoDesenvolvimento.PREOCUPACAO_FAMILIA, "a".repeat(501)));
    }
}
