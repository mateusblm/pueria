package br.com.pueria.pueria.usuarios.infraestrutura.persistencia;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tokens_redefinicao_senha")
public class TokenRedefinicaoSenhaJpaEntidade {
    @Id private UUID id;
    @Column(name = "usuario_id", nullable = false) private UUID usuarioId;
    @Column(name = "token_hash", nullable = false, unique = true, length = 64) private String tokenHash;
    @Column(name = "expira_em", nullable = false) private LocalDateTime expiraEm;
    @Column(name = "usado_em") private LocalDateTime usadoEm;
    @Column(name = "criado_em", nullable = false) private LocalDateTime criadoEm;

    protected TokenRedefinicaoSenhaJpaEntidade() { }

    public TokenRedefinicaoSenhaJpaEntidade(UUID id, UUID usuarioId, String tokenHash, LocalDateTime expiraEm, LocalDateTime usadoEm, LocalDateTime criadoEm) {
        this.id = id; this.usuarioId = usuarioId; this.tokenHash = tokenHash; this.expiraEm = expiraEm; this.usadoEm = usadoEm; this.criadoEm = criadoEm;
    }
    public UUID getId() { return id; }
    public UUID getUsuarioId() { return usuarioId; }
    public String getTokenHash() { return tokenHash; }
    public LocalDateTime getExpiraEm() { return expiraEm; }
    public LocalDateTime getUsadoEm() { return usadoEm; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
}
