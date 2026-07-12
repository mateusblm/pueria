package br.com.pueria.pueria.usuarios.infraestrutura.seguranca;

import br.com.pueria.pueria.usuarios.aplicacao.GeradorTokenRedefinicaoSenha;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class GeradorTokenRedefinicaoSenhaSeguro implements GeradorTokenRedefinicaoSenha {
    private final SecureRandom aleatorio = new SecureRandom();
    @Override public String gerar() { byte[] bytes = new byte[32]; aleatorio.nextBytes(bytes); return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes); }
    @Override public String calcularHash(String token) {
        try { return java.util.HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8))); }
        catch (Exception ex) { throw new IllegalStateException("Não foi possível proteger o token de redefinição", ex); }
    }
}
