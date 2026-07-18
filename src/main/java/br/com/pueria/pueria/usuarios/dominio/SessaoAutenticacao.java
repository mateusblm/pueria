package br.com.pueria.pueria.usuarios.dominio;

import java.time.LocalDateTime;
import java.util.UUID;

public record SessaoAutenticacao(
        UUID id,
        UUID usuarioId,
        String tokenHash,
        LocalDateTime expiraEm,
        LocalDateTime revogadoEm,
        LocalDateTime criadoEm,
        LocalDateTime ultimoUsoEm
) {
    public static SessaoAutenticacao criar(UUID usuarioId, String tokenHash, LocalDateTime expiraEm) {
        return new SessaoAutenticacao(UUID.randomUUID(), usuarioId, tokenHash, expiraEm, null, LocalDateTime.now(), null);
    }

    public boolean estaAtivaEm(LocalDateTime agora) {
        return revogadoEm == null && expiraEm.isAfter(agora);
    }

    public SessaoAutenticacao revogar() {
        return new SessaoAutenticacao(id, usuarioId, tokenHash, expiraEm, LocalDateTime.now(), criadoEm, ultimoUsoEm);
    }
}
