package br.com.pueria.pueria.usuarios.aplicacao;

import br.com.pueria.pueria.comum.excecao.CredenciaisInvalidasException;
import br.com.pueria.pueria.usuarios.dominio.Usuario;
import br.com.pueria.pueria.usuarios.dominio.UsuarioRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AutenticarUsuarioUseCaseTest {

    private UsuarioRepositorioMemoria usuarioRepositorio;
    private AutenticarUsuarioUseCase useCase;

    @BeforeEach
    void setUp() {
        usuarioRepositorio = new UsuarioRepositorioMemoria();
        useCase = new AutenticarUsuarioUseCase(
                usuarioRepositorio,
                new CriptografiaSenhaFake(),
                new GeradorTokenFake()
        );
    }

    @Test
    void deveAutenticarUsuarioComCredenciaisValidas() {
        Usuario usuario = Usuario.cadastrarResponsavel("Mateus", "mateus@email.com", "hash:senha123");
        usuarioRepositorio.salvar(usuario);

        TokenAutenticacao token = useCase.executar(new LoginComando("mateus@email.com", "senha123"));

        assertThat(token.tipo()).isEqualTo("Bearer");
        assertThat(token.token()).isEqualTo("token-" + usuario.getEmail());
        assertThat(token.expiraEmSegundos()).isEqualTo(3600);
    }

    @Test
    void naoDeveAutenticarComSenhaIncorreta() {
        Usuario usuario = Usuario.cadastrarResponsavel("Mateus", "mateus@email.com", "hash:senha123");
        usuarioRepositorio.salvar(usuario);

        assertThatThrownBy(() -> useCase.executar(new LoginComando("mateus@email.com", "senha-errada")))
                .isInstanceOf(CredenciaisInvalidasException.class)
                .hasMessage("E-mail ou senha inválidos");
    }

    @Test
    void naoDeveAutenticarUsuarioInexistente() {
        assertThatThrownBy(() -> useCase.executar(new LoginComando("mateus@email.com", "senha123")))
                .isInstanceOf(CredenciaisInvalidasException.class)
                .hasMessage("E-mail ou senha inválidos");
    }

    private static class CriptografiaSenhaFake implements CriptografiaSenha {

        @Override
        public String criptografar(String senhaPura) {
            return "hash:" + senhaPura;
        }

        @Override
        public boolean corresponde(String senhaPura, String senhaCriptografada) {
            return senhaCriptografada.equals("hash:" + senhaPura);
        }
    }

    private static class GeradorTokenFake implements GeradorToken {

        @Override
        public String gerar(Usuario usuario) {
            return "token-" + usuario.getEmail();
        }

        @Override
        public long expiracaoEmSegundos() {
            return 3600;
        }
    }

    private static class UsuarioRepositorioMemoria implements UsuarioRepositorio {

        private final Map<String, Usuario> usuariosPorEmail = new HashMap<>();

        @Override
        public Usuario salvar(Usuario usuario) {
            usuariosPorEmail.put(normalizar(usuario.getEmail()), usuario);
            return usuario;
        }

        @Override
        public Optional<Usuario> buscarPorId(UUID id) {
            return usuariosPorEmail.values().stream().filter(usuario -> usuario.getId().equals(id)).findFirst();
        }

        @Override
        public Optional<Usuario> buscarPorEmail(String email) {
            return Optional.ofNullable(usuariosPorEmail.get(normalizar(email)));
        }

        @Override
        public boolean existePorEmail(String email) {
            return usuariosPorEmail.containsKey(normalizar(email));
        }

        private String normalizar(String email) {
            return email.trim().toLowerCase(Locale.ROOT);
        }
    }
}
