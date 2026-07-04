package br.com.pueria.pueria.usuarios.aplicacao;

import br.com.pueria.pueria.comum.excecao.RegraDominioException;
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

class CadastrarUsuarioUseCaseTest {

    private UsuarioRepositorioMemoria usuarioRepositorio;
    private CadastrarUsuarioUseCase useCase;

    @BeforeEach
    void setUp() {
        usuarioRepositorio = new UsuarioRepositorioMemoria();
        useCase = new CadastrarUsuarioUseCase(usuarioRepositorio, new CriptografiaSenhaFake());
    }

    @Test
    void deveCadastrarUsuario() {
        UsuarioResumo usuario = useCase.executar(new CadastrarUsuarioComando("Mateus", "mateus@email.com", "senha123"));

        assertThat(usuario.id()).isNotNull();
        assertThat(usuario.nome()).isEqualTo("Mateus");
        assertThat(usuario.email()).isEqualTo("mateus@email.com");
        assertThat(usuarioRepositorio.buscarPorEmail("mateus@email.com")).isPresent();
        assertThat(usuarioRepositorio.buscarPorEmail("mateus@email.com").get().getSenhaCriptografada()).isEqualTo("hash:senha123");
    }

    @Test
    void naoDeveCadastrarEmailDuplicado() {
        useCase.executar(new CadastrarUsuarioComando("Mateus", "mateus@email.com", "senha123"));

        assertThatThrownBy(() -> useCase.executar(new CadastrarUsuarioComando("Outro", "mateus@email.com", "senha456")))
                .isInstanceOf(RegraDominioException.class)
                .hasMessage("Já existe um usuário cadastrado com este e-mail");
    }

    @Test
    void naoDeveCadastrarSenhaCurta() {
        assertThatThrownBy(() -> useCase.executar(new CadastrarUsuarioComando("Mateus", "mateus@email.com", "123")))
                .isInstanceOf(RegraDominioException.class)
                .hasMessage("A senha deve possuir ao menos 8 caracteres");
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
