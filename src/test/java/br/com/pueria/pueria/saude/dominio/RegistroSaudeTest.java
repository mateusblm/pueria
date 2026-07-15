package br.com.pueria.pueria.saude.dominio;

import br.com.pueria.pueria.comum.excecao.RegraDominioException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RegistroSaudeTest {

    @Test
    void registraTextoLivreParaSuplementosEMedicamentos() {
        RegistroSaude registro = RegistroSaude.registrar(UUID.randomUUID(), new DadosRegistroSaude(
                TipoRegistroSaude.MEDICAMENTO_SUPLEMENTO, LocalDate.now(), "Registro informado pela família."));

        assertEquals(TipoRegistroSaude.MEDICAMENTO_SUPLEMENTO, registro.getTipo());
        assertEquals("Registro informado pela família.", registro.getDescricao());
    }

    @Test
    void naoPermiteRegistroSemTexto() {
        assertThrows(RegraDominioException.class, () -> RegistroSaude.registrar(UUID.randomUUID(), new DadosRegistroSaude(
                TipoRegistroSaude.INTERCORRENCIA_CLINICA, LocalDate.now(), "  ")));
    }

    @Test
    void naoPermiteDataNoFuturo() {
        assertThrows(RegraDominioException.class, () -> RegistroSaude.registrar(UUID.randomUUID(), new DadosRegistroSaude(
                TipoRegistroSaude.INTERCORRENCIA_CLINICA, LocalDate.now().plusDays(1), "Registro informado pela família.")));
    }
}
