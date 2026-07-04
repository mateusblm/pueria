package br.com.pueria.pueria.responsaveis.infraestrutura.persistencia;

import br.com.pueria.pueria.responsaveis.dominio.Parentesco;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "responsaveis_criancas")
public class VinculoResponsavelCriancaJpaEntidade {

    @Id
    private UUID id;

    @Column(name = "usuario_id", nullable = false)
    private UUID usuarioId;

    @Column(name = "crianca_id", nullable = false)
    private UUID criancaId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Parentesco parentesco;

    @Column(nullable = false)
    private boolean principal;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    protected VinculoResponsavelCriancaJpaEntidade() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(UUID usuarioId) {
        this.usuarioId = usuarioId;
    }

    public UUID getCriancaId() {
        return criancaId;
    }

    public void setCriancaId(UUID criancaId) {
        this.criancaId = criancaId;
    }

    public Parentesco getParentesco() {
        return parentesco;
    }

    public void setParentesco(Parentesco parentesco) {
        this.parentesco = parentesco;
    }

    public boolean isPrincipal() {
        return principal;
    }

    public void setPrincipal(boolean principal) {
        this.principal = principal;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }
}
