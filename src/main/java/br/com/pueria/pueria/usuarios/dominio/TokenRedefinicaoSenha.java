package br.com.pueria.pueria.usuarios.dominio;

import java.time.LocalDateTime;
import java.util.UUID;

public record TokenRedefinicaoSenha(
        UUID id,
        UUID usuarioId,
        String tokenHash,
        LocalDateTime expiraEm,
        LocalDateTime usadoEm,
        LocalDateTime criadoEm
) {
    public static TokenRedefinicaoSenha criar(UUID usuarioId, String tokenHash, LocalDateTime expiraEm) {
        return new TokenRedefinicaoSenha(UUID.randomUUID(), usuarioId, tokenHash, expiraEm, null, LocalDateTime.now());
    }

    public boolean estaValidoEm(LocalDateTime agora) {
        return usadoEm == null && expiraEm.isAfter(agora);
    }

    public TokenRedefinicaoSenha marcarComoUsado() {
        return new TokenRedefinicaoSenha(id, usuarioId, tokenHash, expiraEm, LocalDateTime.now(), criadoEm);
    }
}
