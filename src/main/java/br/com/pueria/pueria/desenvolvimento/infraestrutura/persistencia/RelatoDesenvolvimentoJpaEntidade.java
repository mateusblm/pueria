package br.com.pueria.pueria.desenvolvimento.infraestrutura.persistencia;

import br.com.pueria.pueria.desenvolvimento.dominio.TipoRelatoDesenvolvimento;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "relatos_desenvolvimento")
public class RelatoDesenvolvimentoJpaEntidade {
    @Id private UUID id;
    @Column(name = "crianca_id", nullable = false) private UUID criancaId;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 40) private TipoRelatoDesenvolvimento tipo;
    @Column(nullable = false, length = 500) private String descricao;
    @Column(name = "registrado_em", nullable = false) private LocalDateTime registradoEm;

    protected RelatoDesenvolvimentoJpaEntidade() { }

    public RelatoDesenvolvimentoJpaEntidade(UUID id, UUID criancaId, TipoRelatoDesenvolvimento tipo, String descricao, LocalDateTime registradoEm) {
        this.id = id; this.criancaId = criancaId; this.tipo = tipo; this.descricao = descricao; this.registradoEm = registradoEm;
    }

    public UUID getId() { return id; }
    public UUID getCriancaId() { return criancaId; }
    public TipoRelatoDesenvolvimento getTipo() { return tipo; }
    public String getDescricao() { return descricao; }
    public LocalDateTime getRegistradoEm() { return registradoEm; }
}
