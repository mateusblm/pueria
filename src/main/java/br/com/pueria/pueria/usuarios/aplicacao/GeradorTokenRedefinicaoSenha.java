package br.com.pueria.pueria.usuarios.aplicacao;

public interface GeradorTokenRedefinicaoSenha {
    String gerar();
    String calcularHash(String token);
}
