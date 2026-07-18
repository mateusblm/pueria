package br.com.pueria.pueria.usuarios.infraestrutura.persistencia;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "sessoes_autenticacao")
public class SessaoAutenticacaoJpaEntidade {
    @Id private UUID id;
    @Column(name = "usuario_id", nullable = false) private UUID usuarioId;
    @Column(name = "token_hash", nullable = false, unique = true, length = 64) private String tokenHash;
    @Column(name = "expira_em", nullable = false) private LocalDateTime expiraEm;
    @Column(name = "revogado_em") private LocalDateTime revogadoEm;
    @Column(name = "criado_em", nullable = false) private LocalDateTime criadoEm;
    @Column(name = "ultimo_uso_em") private LocalDateTime ultimoUsoEm;

    protected SessaoAutenticacaoJpaEntidade() { }

    public SessaoAutenticacaoJpaEntidade(UUID id, UUID usuarioId, String tokenHash, LocalDateTime expiraEm, LocalDateTime revogadoEm, LocalDateTime criadoEm, LocalDateTime ultimoUsoEm) {
        this.id = id; this.usuarioId = usuarioId; this.tokenHash = tokenHash; this.expiraEm = expiraEm; this.revogadoEm = revogadoEm; this.criadoEm = criadoEm; this.ultimoUsoEm = ultimoUsoEm;
    }
    public UUID getId() { return id; }
    public UUID getUsuarioId() { return usuarioId; }
    public String getTokenHash() { return tokenHash; }
    public LocalDateTime getExpiraEm() { return expiraEm; }
    public LocalDateTime getRevogadoEm() { return revogadoEm; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public LocalDateTime getUltimoUsoEm() { return ultimoUsoEm; }
}
