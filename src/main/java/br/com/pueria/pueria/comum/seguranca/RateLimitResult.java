package br.com.pueria.pueria.comum.seguranca;

public record RateLimitResult(boolean permitido, int limite, int restantes, long retryAfterSegundos) {

    public static RateLimitResult permitido(int limite, int restantes) {
        return new RateLimitResult(true, limite, restantes, 0);
    }

    public static RateLimitResult bloqueado(int limite, long retryAfterSegundos) {
        return new RateLimitResult(false, limite, 0, retryAfterSegundos);
    }
}
