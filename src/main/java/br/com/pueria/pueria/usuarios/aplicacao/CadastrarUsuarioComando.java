package br.com.pueria.pueria.usuarios.aplicacao;

public record CadastrarUsuarioComando(
        String nome,
        String email,
        String senha
) {
}
