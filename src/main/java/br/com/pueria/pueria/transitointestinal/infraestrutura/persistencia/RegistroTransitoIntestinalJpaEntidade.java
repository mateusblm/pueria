package br.com.pueria.pueria.transitointestinal.infraestrutura.persistencia;

import br.com.pueria.pueria.transitointestinal.dominio.FacilidadeLimpezaFezes;
import br.com.pueria.pueria.transitointestinal.dominio.TipoFezesBristol;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "registros_transito_intestinal")
public class RegistroTransitoIntestinalJpaEntidade {

    @Id
    private UUID id;

    @Column(name = "crianca_id", nullable = false)
    private UUID criancaId;

    @Column(name = "data_registro", nullable = false)
    private LocalDate dataRegistro;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_fezes", nullable = false, length = 20)
    private TipoFezesBristol tipoFezes;

    @Column(name = "evacuacoes_por_dia")
    private Integer evacuacoesPorDia;

    @Enumerated(EnumType.STRING)
    @Column(name = "facilidade_limpeza", nullable = false, length = 20)
    private FacilidadeLimpezaFezes facilidadeLimpeza;

    private Boolean muco;
    private Boolean restosAlimentares;
    private Boolean raiasSangue;
    private Boolean constipacao;
    private Boolean diarreia;
    private Boolean dorEvacuar;
    private Boolean escapeFecal;
    private Boolean assaduraFrequente;
    private Boolean assaduraVermelhidao;
    private Boolean assaduraPontosVermelhos;
    private Boolean preocupacaoFamilia;

    @Column(length = 1000)
    private String observacao;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    protected RegistroTransitoIntestinalJpaEntidade() {}

    public RegistroTransitoIntestinalJpaEntidade(UUID id, UUID criancaId, LocalDate dataRegistro, TipoFezesBristol tipoFezes, Integer evacuacoesPorDia, FacilidadeLimpezaFezes facilidadeLimpeza, Boolean muco, Boolean restosAlimentares, Boolean raiasSangue, Boolean constipacao, Boolean diarreia, Boolean dorEvacuar, Boolean escapeFecal, Boolean assaduraFrequente, Boolean assaduraVermelhidao, Boolean assaduraPontosVermelhos, Boolean preocupacaoFamilia, String observacao, LocalDateTime criadoEm, LocalDateTime atualizadoEm) {
        this.id = id;
        this.criancaId = criancaId;
        this.dataRegistro = dataRegistro;
        this.tipoFezes = tipoFezes;
        this.evacuacoesPorDia = evacuacoesPorDia;
        this.facilidadeLimpeza = facilidadeLimpeza;
        this.muco = muco;
        this.restosAlimentares = restosAlimentares;
        this.raiasSangue = raiasSangue;
        this.constipacao = constipacao;
        this.diarreia = diarreia;
        this.dorEvacuar = dorEvacuar;
        this.escapeFecal = escapeFecal;
        this.assaduraFrequente = assaduraFrequente;
        this.assaduraVermelhidao = assaduraVermelhidao;
        this.assaduraPontosVermelhos = assaduraPontosVermelhos;
        this.preocupacaoFamilia = preocupacaoFamilia;
        this.observacao = observacao;
        this.criadoEm = criadoEm;
        this.atualizadoEm = atualizadoEm;
    }

    public UUID getId() { return id; }
    public UUID getCriancaId() { return criancaId; }
    public LocalDate getDataRegistro() { return dataRegistro; }
    public TipoFezesBristol getTipoFezes() { return tipoFezes; }
    public Integer getEvacuacoesPorDia() { return evacuacoesPorDia; }
    public FacilidadeLimpezaFezes getFacilidadeLimpeza() { return facilidadeLimpeza; }
    public Boolean getMuco() { return muco; }
    public Boolean getRestosAlimentares() { return restosAlimentares; }
    public Boolean getRaiasSangue() { return raiasSangue; }
    public Boolean getConstipacao() { return constipacao; }
    public Boolean getDiarreia() { return diarreia; }
    public Boolean getDorEvacuar() { return dorEvacuar; }
    public Boolean getEscapeFecal() { return escapeFecal; }
    public Boolean getAssaduraFrequente() { return assaduraFrequente; }
    public Boolean getAssaduraVermelhidao() { return assaduraVermelhidao; }
    public Boolean getAssaduraPontosVermelhos() { return assaduraPontosVermelhos; }
    public Boolean getPreocupacaoFamilia() { return preocupacaoFamilia; }
    public String getObservacao() { return observacao; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
}
