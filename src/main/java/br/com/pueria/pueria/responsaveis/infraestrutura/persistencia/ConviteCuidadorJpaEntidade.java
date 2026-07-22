package br.com.pueria.pueria.responsaveis.infraestrutura.persistencia;

import br.com.pueria.pueria.responsaveis.dominio.EstadoConviteCuidador;
import br.com.pueria.pueria.responsaveis.dominio.Parentesco;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity @Table(name = "convites_cuidadores")
public class ConviteCuidadorJpaEntidade {
    @Id private UUID id;
    @Column(name = "crianca_id", nullable = false) private UUID criancaId;
    @Column(name = "convidado_usuario_id", nullable = false) private UUID convidadoUsuarioId;
    @Column(name = "criado_por_usuario_id", nullable = false) private UUID criadoPorUsuarioId;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private Parentesco parentesco;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private EstadoConviteCuidador estado;
    @Column(name = "criado_em", nullable = false) private LocalDateTime criadoEm;
    @Column(name = "respondido_em") private LocalDateTime respondidoEm;
    protected ConviteCuidadorJpaEntidade() { }
    public UUID getId() { return id; } public void setId(UUID id) { this.id = id; } public UUID getCriancaId() { return criancaId; } public void setCriancaId(UUID value) { criancaId = value; } public UUID getConvidadoUsuarioId() { return convidadoUsuarioId; } public void setConvidadoUsuarioId(UUID value) { convidadoUsuarioId = value; } public UUID getCriadoPorUsuarioId() { return criadoPorUsuarioId; } public void setCriadoPorUsuarioId(UUID value) { criadoPorUsuarioId = value; } public Parentesco getParentesco() { return parentesco; } public void setParentesco(Parentesco value) { parentesco = value; } public EstadoConviteCuidador getEstado() { return estado; } public void setEstado(EstadoConviteCuidador value) { estado = value; } public LocalDateTime getCriadoEm() { return criadoEm; } public void setCriadoEm(LocalDateTime value) { criadoEm = value; } public LocalDateTime getRespondidoEm() { return respondidoEm; } public void setRespondidoEm(LocalDateTime value) { respondidoEm = value; }
}
