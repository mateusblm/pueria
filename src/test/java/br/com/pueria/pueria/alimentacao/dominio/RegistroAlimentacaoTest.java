package br.com.pueria.pueria.alimentacao.dominio;

import br.com.pueria.pueria.comum.excecao.RegraDominioException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RegistroAlimentacaoTest {

    @Test
    void deveRegistrarComValoresPadraoQuandoCamposOpcionaisNaoForemInformados() {
        RegistroAlimentacao registro = RegistroAlimentacao.registrar(UUID.randomUUID(), dadosValidos(null, null, null, null));

        assertEquals(TipoLeiteAlimentacao.NAO_INFORMADO, registro.getTipoLeite());
        assertEquals(EstagioAlimentar.NAO_INFORMADO, registro.getEstagioAlimentar());
        assertEquals(TexturaAlimentar.NAO_INFORMADO, registro.getTexturaPredominante());
    }

    @Test
    void naoDevePermitirDataFutura() {
        assertThrows(RegraDominioException.class, () ->
                RegistroAlimentacao.registrar(UUID.randomUUID(), dadosValidos(LocalDate.now().plusDays(1), null, null, null))
        );
    }

    @Test
    void naoDevePermitirIdadeInicioComplementarForaDoLimite() {
        assertThrows(RegraDominioException.class, () ->
                RegistroAlimentacao.registrar(UUID.randomUUID(), dadosValidos(null, 25, null, null))
        );
    }

    private DadosAlimentacao dadosValidos(LocalDate data, Integer idadeInicio, Integer refeicoes, String observacao) {
        return new DadosAlimentacao(
                data == null ? LocalDate.now() : data,
                null,
                null,
                idadeInicio,
                refeicoes,
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
