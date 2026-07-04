package br.com.pueria.pueria.usuarios.aplicacao;

public record TokenAutenticacao(
        String tipo,
        String token,
        long expiraEmSegundos
) {

    public static TokenAutenticacao bearer(String token, long expiraEmSegundos) {
        return new TokenAutenticacao("Bearer", token, expiraEmSegundos);
    }
}
