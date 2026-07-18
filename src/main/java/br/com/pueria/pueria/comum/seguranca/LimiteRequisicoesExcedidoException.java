package br.com.pueria.pueria.comum.seguranca;

public class LimiteRequisicoesExcedidoException extends RuntimeException {

    private final long retryAfterSegundos;

    public LimiteRequisicoesExcedidoException(long retryAfterSegundos) {
        super("Muitas tentativas. Aguarde um instante antes de tentar novamente.");
        this.retryAfterSegundos = retryAfterSegundos;
    }

    public long getRetryAfterSegundos() {
        return retryAfterSegundos;
    }
}
