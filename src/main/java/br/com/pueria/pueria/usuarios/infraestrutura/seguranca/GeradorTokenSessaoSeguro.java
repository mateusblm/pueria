package br.com.pueria.pueria.usuarios.infraestrutura.seguranca;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;

@Component
public class GeradorTokenSessaoSeguro {
    private final SecureRandom aleatorio = new SecureRandom();
    public String gerar() { byte[] bytes = new byte[32]; aleatorio.nextBytes(bytes); return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes); }
    public String calcularHash(String token) {
        try { return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8))); }
        catch (Exception ex) { throw new IllegalStateException("NÃ£o foi possÃ­vel proteger a sessÃ£o", ex); }
    }
}
