package br.com.pueria.pueria.sono.dominio;

import br.com.pueria.pueria.comum.excecao.RegraDominioException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
                LocalSono.BERCO,
                false,
                false,
                false,
                false,
                false,
                false,
                null
        );
    }
}
