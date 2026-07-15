package br.com.pueria.pueria.saude.infraestrutura.persistencia;

import br.com.pueria.pueria.saude.dominio.TipoRegistroSaude;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "registros_saude_cuidados")
public class RegistroSaudeJpaEntidade {
    @Id private UUID id;
    @Column(name = "crianca_id", nullable = false) private UUID criancaId;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 40) private TipoRegistroSaude tipo;
    @Column(name = "data_registro", nullable = false) private LocalDate dataRegistro;
    @Column(nullable = false, length = 4000) private String descricao;
    @Column(name = "criado_em", nullable = false) private LocalDateTime criadoEm;
    @Column(name = "atualizado_em") private LocalDateTime atualizadoEm;

    protected RegistroSaudeJpaEntidade() { }

    public RegistroSaudeJpaEntidade(UUID id, UUID criancaId, TipoRegistroSaude tipo, LocalDate dataRegistro, String descricao, LocalDateTime criadoEm, LocalDateTime atualizadoEm) {
        this.id = id; this.criancaId = criancaId; this.tipo = tipo; this.dataRegistro = dataRegistro; this.descricao = descricao; this.criadoEm = criadoEm; this.atualizadoEm = atualizadoEm;
    }

    public UUID getId() { return id; }
    public UUID getCriancaId() { return criancaId; }
    public TipoRegistroSaude getTipo() { return tipo; }
    public LocalDate getDataRegistro() { return dataRegistro; }
    public String getDescricao() { return descricao; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
}
