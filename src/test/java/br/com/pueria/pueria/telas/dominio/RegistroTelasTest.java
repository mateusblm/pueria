package br.com.pueria.pueria.telas.dominio;

import br.com.pueria.pueria.comum.excecao.RegraDominioException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RegistroTelasTest {

    @Test
    void deveCalcularMediaPonderadaEntreSemanaEFimDeSemana() {
        RegistroTelas registro = RegistroTelas.registrar(UUID.randomUUID(), dados(30, 90));

        assertEquals(47, registro.minutosMediosDia());
    }

    @Test
    void deveUsarUnicoTempoInformadoComoMedia() {
        RegistroTelas registro = RegistroTelas.registrar(UUID.randomUUID(), dados(40, null));

        assertEquals(40, registro.minutosMediosDia());
    }

    @Test
    void naoDeveAceitarDataFutura() {
        DadosTelas dados = new DadosTelas(
                LocalDate.now().plusDays(1),
                10,
                20,
                TipoConteudoTela.VIDEO_PASSIVO,
                false,
                false,
                false,
                false,
                false,
                true,
                true,
                false,
                false,
                false,
                false,
                true,
                true,
                false,
                null
        );

        assertThrows(RegraDominioException.class, () -> RegistroTelas.registrar(UUID.randomUUID(), dados));
    }

    @Test
    void naoDeveAceitarMinutosForaDoLimiteDiario() {
        assertThrows(RegraDominioException.class, () -> RegistroTelas.registrar(UUID.randomUUID(), dados(1441, null)));
    }

    private static DadosTelas dados(Integer semana, Integer fimSemana) {
        return new DadosTelas(
                LocalDate.now(),
                semana,
                fimSemana,
                TipoConteudoTela.VIDEO_PASSIVO,
                false,
                false,
                false,
                false,
                false,
                true,
                true,
                false,
                false,
                false,
                false,
                true,
                true,
                false,
                null
        );
    }
}
