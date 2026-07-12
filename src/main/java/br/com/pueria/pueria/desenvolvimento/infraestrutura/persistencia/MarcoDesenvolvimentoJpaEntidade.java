package br.com.pueria.pueria.desenvolvimento.infraestrutura.persistencia;

import br.com.pueria.pueria.desenvolvimento.dominio.AreaDesenvolvimento;
import br.com.pueria.pueria.desenvolvimento.dominio.PapelClinicoMarcoDesenvolvimento;
import br.com.pueria.pueria.desenvolvimento.dominio.TipoFonteMarcoDesenvolvimento;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "marcos_desenvolvimento")
public class MarcoDesenvolvimentoJpaEntidade {

    @Id
    private UUID id;

    @Column(name = "idade_meses", nullable = false)
    private int idadeMeses;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private AreaDesenvolvimento area;

    @Column(nullable = false, length = 300)
    private String descricao;

    @Column(nullable = false, length = 120)
    private String fonte;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_fonte", nullable = false, length = 40)
    private TipoFonteMarcoDesenvolvimento tipoFonte;

    @Column(name = "versao_catalogo", nullable = false, length = 40)
    private String versaoCatalogo;

    @Enumerated(EnumType.STRING)
    @Column(name = "papel_clinico", nullable = false, length = 40)
    private PapelClinicoMarcoDesenvolvimento papelClinico;

    @Column(name = "alta_relevancia_vigilancia", nullable = false)
    private boolean altaRelevanciaVigilancia;

    @Column(nullable = false)
    private boolean ativo;

    protected MarcoDesenvolvimentoJpaEntidade() {
    }

    public UUID getId() { return id; }
    public int getIdadeMeses() { return idadeMeses; }
    public AreaDesenvolvimento getArea() { return area; }
    public String getDescricao() { return descricao; }
    public String getFonte() { return fonte; }
    public TipoFonteMarcoDesenvolvimento getTipoFonte() { return tipoFonte; }
    public String getVersaoCatalogo() { return versaoCatalogo; }
    public PapelClinicoMarcoDesenvolvimento getPapelClinico() { return papelClinico; }
    public boolean isAltaRelevanciaVigilancia() { return altaRelevanciaVigilancia; }
    public boolean isAtivo() { return ativo; }
}
