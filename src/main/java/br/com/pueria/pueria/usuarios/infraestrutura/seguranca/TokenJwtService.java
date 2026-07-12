package br.com.pueria.pueria.usuarios.infraestrutura.seguranca;

import br.com.pueria.pueria.comum.excecao.CredenciaisInvalidasException;
import br.com.pueria.pueria.usuarios.aplicacao.GeradorToken;
import br.com.pueria.pueria.usuarios.dominio.Usuario;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class TokenJwtService implements GeradorToken {

    private static final String ALGORITMO_HMAC = "HmacSHA256";

    private final ObjectMapper objectMapper;
    private final String segredo;
    private final long expiracaoSegundos;

    public TokenJwtService(
            ObjectMapper objectMapper,
            @Value("${seguranca.jwt.secret:pueria-dev-secret-altere-este-valor-em-producao-256-bits}") String segredo,
            @Value("${seguranca.jwt.expiracao-segundos:3600}") long expiracaoSegundos
    ) {
        this.objectMapper = objectMapper;
        this.segredo = segredo;
        this.expiracaoSegundos = expiracaoSegundos;
    }

    @Override
    public String gerar(Usuario usuario) {
        long agora = Instant.now().getEpochSecond();

        Map<String, Object> cabecalho = new LinkedHashMap<>();
        cabecalho.put("alg", "HS256");
        cabecalho.put("typ", "JWT");

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("iss", "pueria-api");
        payload.put("sub", usuario.getEmail());
        payload.put("uid", usuario.getId().toString());
        payload.put("tipo", usuario.getTipo().name());
        payload.put("psv", assinaturaSenha(usuario));
        payload.put("iat", agora);
        payload.put("exp", agora + expiracaoSegundos);

        String cabecalhoBase64 = codificarJson(cabecalho);
        String payloadBase64 = codificarJson(payload);
        String conteudoAssinado = cabecalhoBase64 + "." + payloadBase64;
        String assinatura = assinar(conteudoAssinado);

        return conteudoAssinado + "." + assinatura;
    }

    public String validarEObterEmail(String token) {
        return validarEObterAutenticacao(token).email();
    }

    public AutenticacaoJwtValidada validarEObterAutenticacao(String token) {
        try {
            String[] partes = token.split("\\.");
            if (partes.length != 3) {
                throw new CredenciaisInvalidasException("Token JWT inválido");
            }

            String conteudoAssinado = partes[0] + "." + partes[1];
            String assinaturaEsperada = assinar(conteudoAssinado);

            if (!MessageDigest.isEqual(assinaturaEsperada.getBytes(StandardCharsets.UTF_8), partes[2].getBytes(StandardCharsets.UTF_8))) {
                throw new CredenciaisInvalidasException("Token JWT inválido");
            }

            Map<String, Object> payload = decodificarJson(partes[1]);
            Number expiracao = (Number) payload.get("exp");
            if (expiracao == null || expiracao.longValue() <= Instant.now().getEpochSecond()) {
                throw new CredenciaisInvalidasException("Token JWT expirado");
            }

            Object email = payload.get("sub");
            Object assinaturaSenha = payload.get("psv");
            if (email == null || email.toString().isBlank() || assinaturaSenha == null) {
                throw new CredenciaisInvalidasException("Token JWT inválido");
            }

            return new AutenticacaoJwtValidada(email.toString(), assinaturaSenha.toString());
        } catch (CredenciaisInvalidasException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new CredenciaisInvalidasException("Token JWT inválido");
        }
    }

    public boolean correspondeAssinaturaSenha(Usuario usuario, String assinatura) {
        return MessageDigest.isEqual(assinaturaSenha(usuario).getBytes(StandardCharsets.UTF_8), assinatura.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public long expiracaoEmSegundos() {
        return expiracaoSegundos;
    }

    private String codificarJson(Map<String, Object> valores) {
        try {
            byte[] json = objectMapper.writeValueAsBytes(valores);
            return Base64.getUrlEncoder().withoutPadding().encodeToString(json);
        } catch (Exception ex) {
            throw new IllegalStateException("Falha ao gerar token JWT", ex);
        }
    }

    private Map<String, Object> decodificarJson(String valorBase64) {
        try {
            byte[] json = Base64.getUrlDecoder().decode(valorBase64);
            return objectMapper.readValue(json, new TypeReference<>() {
            });
        } catch (Exception ex) {
            throw new CredenciaisInvalidasException("Token JWT inválido");
        }
    }

    private String assinar(String conteudo) {
        try {
            Mac mac = Mac.getInstance(ALGORITMO_HMAC);
            mac.init(new SecretKeySpec(segredo.getBytes(StandardCharsets.UTF_8), ALGORITMO_HMAC));
            byte[] assinatura = mac.doFinal(conteudo.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(assinatura);
        } catch (Exception ex) {
            throw new IllegalStateException("Falha ao assinar token JWT", ex);
        }
    }

    private String assinaturaSenha(Usuario usuario) {
        try { return java.util.HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(usuario.getSenhaCriptografada().getBytes(StandardCharsets.UTF_8))); }
        catch (Exception ex) { throw new IllegalStateException("Falha ao validar a sessão", ex); }
    }

    public record AutenticacaoJwtValidada(String email, String assinaturaSenha) { }
}
