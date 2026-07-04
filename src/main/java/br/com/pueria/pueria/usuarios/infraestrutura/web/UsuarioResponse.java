package br.com.pueria.pueria.usuarios.infraestrutura.web;

import br.com.pueria.pueria.usuarios.aplicacao.UsuarioResumo;
import br.com.pueria.pueria.usuarios.dominio.TipoUsuario;

import java.util.UUID;

public record UsuarioResponse(
        UUID id,
        String nome,
        String email,
        TipoUsuario tipo
) {

    public static UsuarioResponse de(UsuarioResumo usuario) {
        return new UsuarioResponse(usuario.id(), usuario.nome(), usuario.email(), usuario.tipo());
    }
}
