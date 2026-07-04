package br.com.pueria.pueria.usuarios.aplicacao;

import br.com.pueria.pueria.usuarios.dominio.TipoUsuario;
import br.com.pueria.pueria.usuarios.dominio.Usuario;

import java.util.UUID;

public record UsuarioResumo(
        UUID id,
        String nome,
        String email,
        TipoUsuario tipo
) {

    public static UsuarioResumo de(Usuario usuario) {
        return new UsuarioResumo(usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.getTipo());
    }
}
