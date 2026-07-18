package br.com.pueria.pueria.comum.seguranca;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

@Component
public class IdentificadorCliente {

    private final boolean confiarCabecalhosEncaminhados;

    public IdentificadorCliente(@Value("${pueria.rate-limit.confiar-cabecalhos-encaminhados:false}") boolean confiarCabecalhosEncaminhados) {
        this.confiarCabecalhosEncaminhados = confiarCabecalhosEncaminhados;
    }

    public String porIp(HttpServletRequest request) {
        return hash(valorIp(request));
    }

    public String porEmail(String email) {
        return hash(email == null ? "" : email.trim().toLowerCase(Locale.ROOT));
    }

    public String porSessao(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("pueria_refresh".equals(cookie.getName()) && !cookie.getValue().isBlank()) {
                    return hash(cookie.getValue());
                }
            }
        }
        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Bearer ") && authorization.length() > 7) {
            return hash(authorization.substring(7));
        }
        return porIp(request);
    }

    private String valorIp(HttpServletRequest request) {
        if (confiarCabecalhosEncaminhados) {
            String forwardedFor = request.getHeader("X-Forwarded-For");
            if (forwardedFor != null && !forwardedFor.isBlank()) {
                return forwardedFor.split(",", 2)[0].trim();
            }
            String realIp = request.getHeader("X-Real-IP");
            if (realIp != null && !realIp.isBlank()) {
                return realIp.trim();
            }
        }
        return request.getRemoteAddr();
    }

    private String hash(String valor) {
        try {
            byte[] bytes = MessageDigest.getInstance("SHA-256").digest(valor.getBytes(StandardCharsets.UTF_8));
            StringBuilder resultado = new StringBuilder(bytes.length * 2);
            for (byte item : bytes) {
                resultado.append(String.format("%02x", item));
            }
            return resultado.toString();
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 não está disponível.", ex);
        }
    }
}
