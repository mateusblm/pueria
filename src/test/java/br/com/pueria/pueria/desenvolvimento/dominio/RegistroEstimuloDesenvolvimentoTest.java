package br.com.pueria.pueria.desenvolvimento.dominio;

import br.com.pueria.pueria.comum.excecao.RegraDominioException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RegistroEstimuloDesenvolvimentoTest {

    @Test
    void registraAtividadeExperimentadaComObservacaoOpcional() {
        RegistroEstimuloDesenvolvimento registro = RegistroEstimuloDesenvolvimento.registrar(UUID.randomUUID(), UUID.randomUUID(), "  Gostou da brincadeira. ");
        RegistroEstimuloDesenvolvimento semNota = RegistroEstimuloDesenvolvimento.registrar(UUID.randomUUID(), UUID.randomUUID(), " ");

        assertEquals("Gostou da brincadeira.", registro.observacao());
        assertNull(semNota.observacao());
    }

    @Test
    void limitaObservacaoParaManterRegistroObjetivo() {
        assertThrows(RegraDominioException.class, () -> RegistroEstimuloDesenvolvimento.registrar(UUID.randomUUID(), UUID.randomUUID(), "a".repeat(501)));
    }
}
