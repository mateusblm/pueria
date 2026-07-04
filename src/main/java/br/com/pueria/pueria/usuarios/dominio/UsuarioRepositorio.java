package br.com.pueria.pueria.usuarios.dominio;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepositorio {

    Usuario salvar(Usuario usuario);

    Optional<Usuario> buscarPorId(UUID id);

    Optional<Usuario> buscarPorEmail(String email);

    boolean existePorEmail(String email);
}
