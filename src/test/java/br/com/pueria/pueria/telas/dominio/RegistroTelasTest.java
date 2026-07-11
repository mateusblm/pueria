package br.com.pueria.pueria.telas.dominio;

import br.com.pueria.pueria.comum.excecao.RegraDominioException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;
import java.util.List;

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
                List.of(),
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
                false,
                null
        );

        assertThrows(RegraDominioException.class, () -> RegistroTelas.registrar(UUID.randomUUID(), dados));
    }

    @Test
    void naoDeveAceitarMinutosForaDoLimiteDiario() {
        assertThrows(RegraDominioException.class, () -> RegistroTelas.registrar(UUID.randomUUID(), dados(1441, null)));
    }

    @Test
    void deveManterConteudoAssociadoAoDispositivo() {
        DadosTelas dados = new DadosTelas(LocalDate.now(), 30, 30, TipoConteudoTela.VIDEO_PASSIVO,
                List.of(new ContextoUsoTela(TipoDispositivoTela.TV, TipoConteudoTela.VIDEO_PASSIVO),
                        new ContextoUsoTela(TipoDispositivoTela.CELULAR, TipoConteudoTela.VIDEOCHAMADA)),
                false, false, false, false, false, true, true, false, false, false, false, false, true, true, false, null);

        RegistroTelas registro = RegistroTelas.registrar(UUID.randomUUID(), dados);

        assertEquals(2, registro.getContextosUso().size());
        assertEquals(TipoConteudoTela.VIDEOCHAMADA, registro.getContextosUso().get(1).conteudo());
    }

    private static DadosTelas dados(Integer semana, Integer fimSemana) {
        return new DadosTelas(
                LocalDate.now(),
                semana,
                fimSemana,
                TipoConteudoTela.VIDEO_PASSIVO,
                List.of(),
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
                false,
                null
        );
    }
}
