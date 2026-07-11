package br.com.pueria.pueria.sono.dominio;

import br.com.pueria.pueria.comum.excecao.RegraDominioException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegistroSonoTest {

    @Test
    void deveCalcularSonoNoturnoVirandoOMeiaNoite() {
        RegistroSono registro = RegistroSono.registrar(UUID.randomUUID(), dados(LocalTime.of(20, 30), LocalTime.of(6, 0), 90));

        assertEquals(570, registro.minutosSonoNoturno());
        assertEquals(660, registro.minutosSonoTotal24h());
    }

    @Test
    void naoDevePermitirHorarioParcial() {
        assertThrows(RegraDominioException.class, () ->
                RegistroSono.registrar(UUID.randomUUID(), dados(LocalTime.of(20, 30), null, 90))
        );
    }

    @Test
    void naoDevePermitirDataFutura() {
        DadosSono dados = new DadosSono(
                LocalDate.now().plusDays(1),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        assertThrows(RegraDominioException.class, () -> RegistroSono.registrar(UUID.randomUUID(), dados));
    }

    private DadosSono dados(LocalTime dormiu, LocalTime acordou, Integer minutosCochilos) {
        return new DadosSono(
                LocalDate.now(),
                dormiu,
                acordou,
                2,
                minutosCochilos,
                1,
                false,
                true,
                false,
                SuperficieSono.BERCO,
                AmbienteSono.QUARTO_DOS_RESPONSAVEIS,
                List.of(TipoDespertarNoturno.ACORDA_E_MAMA),
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                null
        );
    }

    @Test
    void deveRegistrarDetalhesDaRotinaNoturnaSemDuplicarDespertares() {
        DadosSono dados = new DadosSono(LocalDate.now(), null, null, null, null, 2, false, true, false,
                SuperficieSono.BERCO, AmbienteSono.QUARTO_DOS_RESPONSAVEIS,
                List.of(TipoDespertarNoturno.ACORDA_E_MAMA, TipoDespertarNoturno.ACORDA_E_MAMA), false, false,
                false, true, true, false, false, false, null);

        RegistroSono registro = RegistroSono.registrar(UUID.randomUUID(), dados);

        assertEquals(SuperficieSono.BERCO, registro.getSuperficieSono());
        assertEquals(AmbienteSono.QUARTO_DOS_RESPONSAVEIS, registro.getAmbienteSono());
        assertEquals(List.of(TipoDespertarNoturno.ACORDA_E_MAMA), registro.getTiposDespertarNoturno());
        assertTrue(registro.getRangerDentesDuranteSono());
        assertTrue(registro.getAcordaBemDisposto());
    }
}
