package br.com.pueria.pueria.transitointestinal.dominio;

import br.com.pueria.pueria.comum.excecao.RegraDominioException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RegistroTransitoIntestinalTest {

    @Test
    void deveRegistrarComValoresPadraoQuandoCamposOpcionaisNaoForemInformados() {
        RegistroTransitoIntestinal registro = RegistroTransitoIntestinal.registrar(UUID.randomUUID(), dadosValidos(null, null, null, null));

        assertEquals(TipoFezesBristol.NAO_INFORMADO, registro.getTipoFezes());
        assertEquals(FacilidadeLimpezaFezes.NAO_INFORMADO, registro.getFacilidadeLimpeza());
    }

    @Test
    void naoDevePermitirDataFutura() {
        assertThrows(RegraDominioException.class, () ->
                RegistroTransitoIntestinal.registrar(UUID.randomUUID(), dadosValidos(LocalDate.now().plusDays(1), null, null, null))
        );
    }

    @Test
    void naoDevePermitirEvacuacoesForaDoLimite() {
        assertThrows(RegraDominioException.class, () ->
                RegistroTransitoIntestinal.registrar(UUID.randomUUID(), dadosValidos(null, 31, null, null))
        );
    }

    private DadosTransitoIntestinal dadosValidos(LocalDate data, Integer evacuacoes, TipoFezesBristol tipo, String observacao) {
        return new DadosTransitoIntestinal(
                data == null ? LocalDate.now() : data,
                tipo,
                evacuacoes,
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
                observacao
        );
    }
}
