package br.com.pueria.pueria.transitointestinal.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

class TipoFezesBristolTest {

    @Test
    void descreveTipoSelecionadoComLinguagemLegivel() {
        assertEquals("Tipo 4 (macia e lisa)", TipoFezesBristol.TIPO_4.descricaoParaResumo());
    }

    @Test
    void naoExibeCodigoTecnicoQuandoConsistenciaNaoFoiInformada() {
        String descricao = TipoFezesBristol.NAO_INFORMADO.descricaoParaResumo();

        assertEquals("Consistência não informada", descricao);
        assertFalse(descricao.contains("NAO_INFORMADO"));
    }
}
