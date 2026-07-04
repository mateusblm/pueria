package br.com.pueria.pueria.usuarios.infraestrutura.seguranca;

import br.com.pueria.pueria.comum.excecao.CredenciaisInvalidasException;
import br.com.pueria.pueria.usuarios.dominio.Usuario;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TokenJwtServiceTest {

    @Test
    void deveGerarEValidarToken() {
        TokenJwtService tokenJwtService = new TokenJwtService(
                new ObjectMapper(),
                "segredo-de-teste-com-tamanho-suficiente-para-hmac",
                3600
        );
        Usuario usuario = Usuario.cadastrarResponsavel("Mateus", "mateus@email.com", "senha-criptografada");

        String token = tokenJwtService.gerar(usuario);
        String email = tokenJwtService.validarEObterEmail(token);

        assertThat(token).isNotBlank();
        assertThat(token.split("\\.")).hasSize(3);
        assertThat(email).isEqualTo("mateus@email.com");
    }

    @Test
    void naoDeveValidarTokenAdulterado() {
        TokenJwtService tokenJwtService = new TokenJwtService(
                new ObjectMapper(),
                "segredo-de-teste-com-tamanho-suficiente-para-hmac",
                3600
        );
        Usuario usuario = Usuario.cadastrarResponsavel("Mateus", "mateus@email.com", "senha-criptografada");
        String token = tokenJwtService.gerar(usuario) + "adulterado";

        assertThatThrownBy(() -> tokenJwtService.validarEObterEmail(token))
                .isInstanceOf(CredenciaisInvalidasException.class)
                .hasMessage("Token JWT inválido");
    }

    @Test
    void naoDeveValidarTokenExpirado() throws InterruptedException {
        TokenJwtService tokenJwtService = new TokenJwtService(
                new ObjectMapper(),
                "segredo-de-teste-com-tamanho-suficiente-para-hmac",
                1
        );
        Usuario usuario = Usuario.cadastrarResponsavel("Mateus", "mateus@email.com", "senha-criptografada");
        String token = tokenJwtService.gerar(usuario);

        Thread.sleep(1100);

        assertThatThrownBy(() -> tokenJwtService.validarEObterEmail(token))
                .isInstanceOf(CredenciaisInvalidasException.class)
                .hasMessage("Token JWT expirado");
    }
}
