package br.com.pueria.pueria.usuarios.dominio;

import br.com.pueria.pueria.comum.excecao.RegraDominioException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UsuarioTest {

    @Test
    void deveCadastrarUsuarioResponsavel() {
        Usuario usuario = Usuario.cadastrarResponsavel("Mateus", "MATEUS@EMAIL.COM", "senha-criptografada");

        assertThat(usuario.getId()).isNotNull();
        assertThat(usuario.getNome()).isEqualTo("Mateus");
        assertThat(usuario.getEmail()).isEqualTo("mateus@email.com");
        assertThat(usuario.getSenhaCriptografada()).isEqualTo("senha-criptografada");
        assertThat(usuario.getTipo()).isEqualTo(TipoUsuario.RESPONSAVEL);
        assertThat(usuario.isAtivo()).isTrue();
        assertThat(usuario.getCriadoEm()).isNotNull();
    }

    @Test
    void naoDeveCadastrarUsuarioComEmailInvalido() {
        assertThatThrownBy(() -> Usuario.cadastrarResponsavel("Mateus", "email-invalido", "senha-criptografada"))
                .isInstanceOf(RegraDominioException.class)
                .hasMessage("O e-mail do usuário é inválido");
    }

    @Test
    void naoDeveCadastrarUsuarioSemNome() {
        assertThatThrownBy(() -> Usuario.cadastrarResponsavel(" ", "mateus@email.com", "senha-criptografada"))
                .isInstanceOf(RegraDominioException.class)
                .hasMessage("O nome do usuário é obrigatório");
    }

    @Test
    void naoDeveCadastrarUsuarioSemSenhaCriptografada() {
        assertThatThrownBy(() -> Usuario.cadastrarResponsavel("Mateus", "mateus@email.com", " "))
                .isInstanceOf(RegraDominioException.class)
                .hasMessage("A senha criptografada do usuário é obrigatória");
    }
}
