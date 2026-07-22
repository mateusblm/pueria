package br.com.pueria.pueria.usuarios.aplicacao;

import br.com.pueria.pueria.comum.excecao.RegraDominioException;
import br.com.pueria.pueria.usuarios.dominio.Usuario;
import br.com.pueria.pueria.usuarios.dominio.UsuarioRepositorio;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AtualizarCredenciaisUsuarioUseCaseTest {
    private final Repositorio repositorio = new Repositorio();
    private final AtualizarCredenciaisUsuarioUseCase useCase = new AtualizarCredenciaisUsuarioUseCase(repositorio, new Cripto());

    @Test void atualizaEmailAposConfirmarSenhaAtual() {
        repositorio.salvar(Usuario.cadastrarResponsavel("Marina", "marina@antigo.com", "hash:segura123"));
        useCase.email("marina@antigo.com", "marina@novo.com", "segura123");
        assertThat(repositorio.buscarPorEmail("marina@novo.com")).isPresent();
    }

    @Test void recusaSenhaAtualIncorreta() {
        repositorio.salvar(Usuario.cadastrarResponsavel("Marina", "marina@email.com", "hash:segura123"));
        assertThatThrownBy(() -> useCase.senha("marina@email.com", "errada", "nova1234")).isInstanceOf(RegraDominioException.class).hasMessage("A senha atual não confere");
    }

    @Test void recusaEmailJaEmUso() {
        repositorio.salvar(Usuario.cadastrarResponsavel("Marina", "marina@email.com", "hash:segura123")); repositorio.salvar(Usuario.cadastrarResponsavel("Rafael", "rafael@email.com", "hash:segura123"));
        assertThatThrownBy(() -> useCase.email("marina@email.com", "rafael@email.com", "segura123")).isInstanceOf(RegraDominioException.class);
    }

    private static class Cripto implements CriptografiaSenha { public String criptografar(String senha) { return "hash:" + senha; } public boolean corresponde(String senha, String hash) { return hash.equals("hash:" + senha); } }
    private static class Repositorio implements UsuarioRepositorio { private final Map<String, Usuario> dados = new HashMap<>(); public Usuario salvar(Usuario u) { dados.entrySet().removeIf(e -> e.getValue().getId().equals(u.getId())); dados.put(u.getEmail(), u); return u; } public Optional<Usuario> buscarPorId(UUID id) { return dados.values().stream().filter(u -> u.getId().equals(id)).findFirst(); } public Optional<Usuario> buscarPorEmail(String email) { return Optional.ofNullable(dados.get(email.toLowerCase())); } public boolean existePorEmail(String email) { return dados.containsKey(email.toLowerCase()); } }
}
