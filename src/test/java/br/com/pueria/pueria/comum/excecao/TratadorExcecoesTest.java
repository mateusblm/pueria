package br.com.pueria.pueria.comum.excecao;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class TratadorExcecoesTest {

    private final TratadorExcecoes tratador = new TratadorExcecoes();

    @Test
    void deveRetornarConflitoQuandoUsuarioJaExisteComEmail() {
        ResponseEntity<ErroApi> resposta = tratador.tratarRegraDominio(
                new RegraDominioException("Já existe um usuário cadastrado com este e-mail")
        );

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(resposta.getBody()).isNotNull();
        assertThat(resposta.getBody().status()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(resposta.getBody().mensagens()).containsExactly("Já existe um usuário cadastrado com este e-mail");
    }

    @Test
    void deveRetornarBadRequestParaRegraDeDominioComum() {
        ResponseEntity<ErroApi> resposta = tratador.tratarRegraDominio(
                new RegraDominioException("A senha deve possuir ao menos 8 caracteres")
        );

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(resposta.getBody()).isNotNull();
        assertThat(resposta.getBody().status()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void deveRetornarMensagemSeguraParaViolacaoDeIntegridade() {
        ResponseEntity<ErroApi> resposta = tratador.tratarViolacaoDeIntegridade(
                new DataIntegrityViolationException("detalhe interno do banco")
        );

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(resposta.getBody()).isNotNull();
        assertThat(resposta.getBody().erro()).isEqualTo("Não foi possível salvar o registro");
        assertThat(resposta.getBody().mensagens()).containsExactly("Revise os dados informados e tente novamente.");
    }
}
