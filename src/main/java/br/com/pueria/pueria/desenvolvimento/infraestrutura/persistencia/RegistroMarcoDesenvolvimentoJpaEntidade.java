package br.com.pueria.pueria.desenvolvimento.infraestrutura.persistencia;

import br.com.pueria.pueria.desenvolvimento.dominio.StatusMarcoDesenvolvimento;
import br.com.pueria.pueria.desenvolvimento.dominio.ModalidadeRegistroMarcoDesenvolvimento;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "registros_marcos_desenvolvimento")
public class RegistroMarcoDesenvolvimentoJpaEntidade {

    @Id
    private UUID id;

    @Column(name = "crianca_id", nullable = false)
    private UUID criancaId;

    @Column(name = "marco_id", nullable = false)
    private UUID marcoId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private StatusMarcoDesenvolvimento status;

    @Column(length = 500)
    private String observacao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private ModalidadeRegistroMarcoDesenvolvimento modalidade;

    @Column(name = "registrado_em", nullable = false)
    private LocalDateTime registradoEm;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    protected RegistroMarcoDesenvolvimentoJpaEntidade() {
    }

    public RegistroMarcoDesenvolvimentoJpaEntidade(UUID id, UUID criancaId, UUID marcoId, StatusMarcoDesenvolvimento status, ModalidadeRegistroMarcoDesenvolvimento modalidade, String observacao, LocalDateTime registradoEm, LocalDateTime atualizadoEm) {
        this.id = id;
        this.criancaId = criancaId;
        this.marcoId = marcoId;
        this.status = status;
        this.modalidade = modalidade;
        this.observacao = observacao;
        this.registradoEm = registradoEm;
        this.atualizadoEm = atualizadoEm;
    }

    public UUID getId() { return id; }
    public UUID getCriancaId() { return criancaId; }
    public UUID getMarcoId() { return marcoId; }
    public StatusMarcoDesenvolvimento getStatus() { return status; }
    public ModalidadeRegistroMarcoDesenvolvimento getModalidade() { return modalidade; }
    public String getObservacao() { return observacao; }
    public LocalDateTime getRegistradoEm() { return registradoEm; }
    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
}
