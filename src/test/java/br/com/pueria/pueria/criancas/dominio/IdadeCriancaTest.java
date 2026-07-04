package br.com.pueria.pueria.criancas.dominio;

import br.com.pueria.pueria.comum.excecao.RegraDominioException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IdadeCriancaTest {

    @Test
    void deveCalcularIdadeEmMesesEDias() {
        IdadeCrianca idade = IdadeCrianca.calcular(
                LocalDate.of(2024, 1, 10),
                LocalDate.of(2024, 4, 10)
        );

        assertEquals(3, idade.mesesCompletos());
        assertEquals(91, idade.diasDeVida());
    }

    @Test
    void naoDeveCalcularQuandoNascimentoForDepoisDaReferencia() {
        RegraDominioException exception = assertThrows(RegraDominioException.class, () -> IdadeCrianca.calcular(
                LocalDate.of(2024, 4, 11),
                LocalDate.of(2024, 4, 10)
        ));

        assertEquals("A data de nascimento não pode ser posterior à data de referência.", exception.getMessage());
    }
}
