package br.com.pueria.pueria.desenvolvimento.dominio;

import br.com.pueria.pueria.criancas.dominio.Crianca;
import br.com.pueria.pueria.criancas.dominio.Sexo;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IdadeReferenciaDesenvolvimentoTest {

    @Test
    void usaIdadeCorrigidaParaPrematuraAntesDeVinteEQuatroMesesCorrigidos() {
        Crianca crianca = Crianca.cadastrar("Lia", LocalDate.of(2025, 1, 1), Sexo.FEMININO, true, 32, 1800);

        int meses = IdadeReferenciaDesenvolvimento.mesesParaCheckpoints(crianca, LocalDate.of(2025, 7, 1));

        assertEquals(4, meses);
    }

    @Test
    void voltaAUsarIdadeCronologicaAposVinteEQuatroMesesCorrigidos() {
        Crianca crianca = Crianca.cadastrar("Lia", LocalDate.of(2023, 1, 1), Sexo.FEMININO, true, 32, 1800);

        int meses = IdadeReferenciaDesenvolvimento.mesesParaCheckpoints(crianca, LocalDate.of(2025, 4, 1));

        assertEquals(27, meses);
    }
}
