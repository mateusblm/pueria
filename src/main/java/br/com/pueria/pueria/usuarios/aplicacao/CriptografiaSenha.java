package br.com.pueria.pueria.usuarios.aplicacao;

public interface CriptografiaSenha {

    String criptografar(String senhaPura);

    boolean corresponde(String senhaPura, String senhaCriptografada);
}
