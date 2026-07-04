package br.com.pueria.pueria.usuarios.infraestrutura.web;

import br.com.pueria.pueria.usuarios.aplicacao.TokenAutenticacao;

public record AuthResponse(
        String tipo,
        String token,
        long expiraEmSegundos
) {

    public static AuthResponse de(TokenAutenticacao token) {
        return new AuthResponse(token.tipo(), token.token(), token.expiraEmSegundos());
    }
}
