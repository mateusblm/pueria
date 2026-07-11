package br.com.pueria.pueria.criancas.dominio;

import br.com.pueria.pueria.comum.excecao.RegraDominioException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ContextoClinicoCriancaTest {

    @Test
    void deveUsarOpcoesNaoInformadasQuandoContextoNaoFoiPreenchido() {
        ContextoClinicoCrianca contexto = ContextoClinicoCrianca.naoInformado();

        assertEquals(TipoGestacao.NAO_INFORMADO, contexto.tipoGestacao());
        assertEquals(StatusCondicaoClinica.PREFIRO_INFORMAR_DEPOIS, contexto.statusT21());
        assertEquals(StatusCondicaoClinica.PREFIRO_INFORMAR_DEPOIS, contexto.statusTurner());
        assertFalse(contexto.outraCondicaoRelevante());
        assertNull(contexto.observacoesCondicaoRelevante());
    }

    @Test
    void devePreservarContextoInformadoELimparObservacao() {
        ContextoClinicoCrianca contexto = new ContextoClinicoCrianca(
                TipoGestacao.MULTIPLA,
                StatusCondicaoClinica.EM_INVESTIGACAO,
                StatusCondicaoClinica.NAO,
                true,
                "  Acompanhamento com neurologista.  "
        );

        assertEquals(TipoGestacao.MULTIPLA, contexto.tipoGestacao());
        assertEquals(StatusCondicaoClinica.EM_INVESTIGACAO, contexto.statusT21());
        assertEquals(StatusCondicaoClinica.NAO, contexto.statusTurner());
        assertEquals("Acompanhamento com neurologista.", contexto.observacoesCondicaoRelevante());
    }

    @Test
    void naoDeveAceitarObservacaoAcimaDoLimite() {
        assertThrows(RegraDominioException.class, () -> new ContextoClinicoCrianca(
                TipoGestacao.UNICA,
                StatusCondicaoClinica.NAO,
                StatusCondicaoClinica.NAO,
                true,
                "a".repeat(1001)
        ));
    }
}
