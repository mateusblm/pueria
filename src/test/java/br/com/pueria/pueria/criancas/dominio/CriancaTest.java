package br.com.pueria.pueria.criancas.dominio;

import br.com.pueria.pueria.comum.excecao.RegraDominioException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CriancaTest {

    @Test
    void deveCadastrarCriancaComDadosValidos() {
        Crianca crianca = Crianca.cadastrar(
                " Ana Clara ",
                LocalDate.of(2024, 1, 10),
                Sexo.FEMININO,
                false,
                39,
                3200
        );

        assertNotNull(crianca.getId());
        assertEquals("Ana Clara", crianca.getNome());
        assertEquals("ana clara", crianca.getNomeNormalizado());
        assertEquals(Sexo.FEMININO, crianca.getSexo());
        assertEquals(39, crianca.getSemanasGestacionais());
        assertEquals(3200, crianca.getPesoNascimentoGramas());
    }

    @Test
    void deveAssumirSexoNaoInformadoQuandoSexoForNulo() {
        Crianca crianca = Crianca.cadastrar(
                "Pedro",
                LocalDate.of(2024, 2, 15),
                null,
                false,
                39,
                3200
        );

        assertEquals(Sexo.NAO_INFORMADO, crianca.getSexo());
    }

    @Test
    void naoDeveCadastrarCriancaSemNome() {
        RegraDominioException exception = assertThrows(RegraDominioException.class, () -> Crianca.cadastrar(
                " ",
                LocalDate.of(2024, 1, 10),
                Sexo.FEMININO,
                false,
                39,
                3200
        ));

        assertEquals("O nome da criança é obrigatório.", exception.getMessage());
    }

    @Test
    void naoDeveCadastrarCriancaComDataNascimentoFutura() {
        RegraDominioException exception = assertThrows(RegraDominioException.class, () -> Crianca.cadastrar(
                "Ana",
                LocalDate.now().plusDays(1),
                Sexo.FEMININO,
                false,
                39,
                3200
        ));

        assertEquals("A data de nascimento não pode estar no futuro.", exception.getMessage());
    }

    @Test
    void naoDeveCadastrarSemanasGestacionaisNulas() {
        RegraDominioException exception = assertThrows(RegraDominioException.class, () -> Crianca.cadastrar(
                "Ana",
                LocalDate.of(2024, 1, 10),
                Sexo.FEMININO,
                false,
                null,
                3200
        ));

        assertEquals("As semanas gestacionais são obrigatórias.", exception.getMessage());
    }

    @Test
    void naoDeveCadastrarPesoNascimentoNulo() {
        RegraDominioException exception = assertThrows(RegraDominioException.class, () -> Crianca.cadastrar(
                "Ana",
                LocalDate.of(2024, 1, 10),
                Sexo.FEMININO,
                false,
                39,
                null
        ));

        assertEquals("O peso de nascimento é obrigatório.", exception.getMessage());
    }

    @Test
    void naoDeveCadastrarPrematuraComTrintaESeteSemanasOuMais() {
        RegraDominioException exception = assertThrows(RegraDominioException.class, () -> Crianca.cadastrar(
                "Ana",
                LocalDate.of(2024, 1, 10),
                Sexo.FEMININO,
                true,
                37,
                2500
        ));

        assertEquals("Uma criança marcada como prematura deve ter menos de 37 semanas gestacionais.", exception.getMessage());
    }

    @Test
    void naoDeveCadastrarNaoPrematuraComMenosDeTrintaESeteSemanas() {
        RegraDominioException exception = assertThrows(RegraDominioException.class, () -> Crianca.cadastrar(
                "Ana",
                LocalDate.of(2024, 1, 10),
                Sexo.FEMININO,
                false,
                36,
                2500
        ));

        assertEquals("Uma criança com menos de 37 semanas gestacionais deve ser marcada como prematura.", exception.getMessage());
    }

    @Test
    void devePermitirPrematuraComMenosDeTrintaESeteSemanas() {
        Crianca crianca = Crianca.cadastrar(
                "Ana",
                LocalDate.of(2024, 1, 10),
                Sexo.FEMININO,
                true,
                35,
                2500
        );

        assertTrue(crianca.isPrematura());
        assertEquals(35, crianca.getSemanasGestacionais());
    }

    @Test
    void naoDeveCadastrarPesoNascimentoForaDoLimiteOperacional() {
        RegraDominioException exception = assertThrows(RegraDominioException.class, () -> Crianca.cadastrar(
                "Ana",
                LocalDate.of(2024, 1, 10),
                Sexo.FEMININO,
                false,
                39,
                100
        ));

        assertEquals("O peso de nascimento informado está fora do limite operacional permitido.", exception.getMessage());
    }
}
