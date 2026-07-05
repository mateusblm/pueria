package br.com.pueria.pueria.crescimento.infraestrutura.persistencia;

import br.com.pueria.pueria.crescimento.dominio.OrigemMedidaCrescimento;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "medidas_crescimento")
public class MedidaCrescimentoJpaEntidade {

    @Id
    private UUID id;

    @Column(name = "crianca_id", nullable = false)
    private UUID criancaId;

    @Column(name = "data_medicao", nullable = false)
    private LocalDate dataMedicao;

    @Column(name = "peso_kg", precision = 5, scale = 2)
    private BigDecimal pesoKg;

    @Column(name = "comprimento_cm", precision = 5, scale = 1)
    private BigDecimal comprimentoCm;

    @Column(name = "perimetro_cefalico_cm", precision = 4, scale = 1)
    private BigDecimal perimetroCefalicoCm;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OrigemMedidaCrescimento origem;

    @Column(length = 500)
    private String observacao;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    protected MedidaCrescimentoJpaEntidade() {}

    public MedidaCrescimentoJpaEntidade(UUID id, UUID criancaId, LocalDate dataMedicao, BigDecimal pesoKg, BigDecimal comprimentoCm, BigDecimal perimetroCefalicoCm, OrigemMedidaCrescimento origem, String observacao, LocalDateTime criadoEm, LocalDateTime atualizadoEm) {
        this.id = id;
        this.criancaId = criancaId;
        this.dataMedicao = dataMedicao;
        this.pesoKg = pesoKg;
        this.comprimentoCm = comprimentoCm;
        this.perimetroCefalicoCm = perimetroCefalicoCm;
        this.origem = origem;
        this.observacao = observacao;
        this.criadoEm = criadoEm;
        this.atualizadoEm = atualizadoEm;
    }

    public UUID getId() { return id; }
    public UUID getCriancaId() { return criancaId; }
    public LocalDate getDataMedicao() { return dataMedicao; }
    public BigDecimal getPesoKg() { return pesoKg; }
    public BigDecimal getComprimentoCm() { return comprimentoCm; }
    public BigDecimal getPerimetroCefalicoCm() { return perimetroCefalicoCm; }
    public OrigemMedidaCrescimento getOrigem() { return origem; }
    public String getObservacao() { return observacao; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
}
