package br.com.pueria.pueria.comum.excecao;

public class CredenciaisInvalidasException extends RuntimeException {

    public CredenciaisInvalidasException() {
        super("E-mail ou senha inválidos");
    }

    public CredenciaisInvalidasException(String mensagem) {
        super(mensagem);
    }
}
