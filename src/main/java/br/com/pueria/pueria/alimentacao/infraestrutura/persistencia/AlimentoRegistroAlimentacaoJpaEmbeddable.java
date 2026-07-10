package br.com.pueria.pueria.alimentacao.infraestrutura.persistencia;

import br.com.pueria.pueria.alimentacao.dominio.AceitacaoAlimento;
import br.com.pueria.pueria.alimentacao.dominio.ClassificacaoGluten;
import br.com.pueria.pueria.alimentacao.dominio.GrupoAlimento;
import br.com.pueria.pueria.alimentacao.dominio.SituacaoSinaisOferta;
import br.com.pueria.pueria.alimentacao.dominio.TexturaAlimentar;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Convert;

import java.time.LocalDate;
import java.util.List;

@Embeddable
public class AlimentoRegistroAlimentacaoJpaEmbeddable {

    @Column(name = "codigo", nullable = false, length = 80)
    private String codigo;

    @Column(name = "nome", nullable = false, length = 120)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(name = "grupo", nullable = false, length = 40)
    private GrupoAlimento grupo;

    private Boolean alergenico;
    private LocalDate dataIntroducao;

    @Column(length = 160)
    private String formaPreparo;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private TexturaAlimentar textura;

    @Column(length = 80)
    private String quantidadeAproximada;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private AceitacaoAlimento aceitacao;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private ClassificacaoGluten classificacaoGluten;

    @Column(length = 120)
    private String tipoPeixe;

    @Convert(converter = ListaDatasJsonConverter.class)
    @Column(length = 2000)
    private List<LocalDate> datasReexposicao;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private SituacaoSinaisOferta situacaoSinais;

    private Boolean repetiuOutroDia;
    private Boolean sintomasPele;
    private Boolean sintomasIntestinais;
    private Boolean sintomasRespiratorios;
    private Boolean alteracaoSono;
    private Boolean alteracaoComportamento;

    @Column(length = 500)
    private String observacao;

    protected AlimentoRegistroAlimentacaoJpaEmbeddable() {}

    public AlimentoRegistroAlimentacaoJpaEmbeddable(
            String codigo,
            String nome,
            GrupoAlimento grupo,
            Boolean alergenico,
            LocalDate dataIntroducao,
            String formaPreparo,
            TexturaAlimentar textura,
            String quantidadeAproximada,
            AceitacaoAlimento aceitacao,
            ClassificacaoGluten classificacaoGluten,
            String tipoPeixe,
            List<LocalDate> datasReexposicao,
            SituacaoSinaisOferta situacaoSinais,
            Boolean repetiuOutroDia,
            Boolean sintomasPele,
            Boolean sintomasIntestinais,
            Boolean sintomasRespiratorios,
            Boolean alteracaoSono,
            Boolean alteracaoComportamento,
            String observacao
    ) {
        this.codigo = codigo;
        this.nome = nome;
        this.grupo = grupo;
        this.alergenico = alergenico;
        this.dataIntroducao = dataIntroducao;
        this.formaPreparo = formaPreparo;
        this.textura = textura;
        this.quantidadeAproximada = quantidadeAproximada;
        this.aceitacao = aceitacao;
        this.classificacaoGluten = classificacaoGluten;
        this.tipoPeixe = tipoPeixe;
        this.datasReexposicao = datasReexposicao;
        this.situacaoSinais = situacaoSinais;
        this.repetiuOutroDia = repetiuOutroDia;
        this.sintomasPele = sintomasPele;
        this.sintomasIntestinais = sintomasIntestinais;
        this.sintomasRespiratorios = sintomasRespiratorios;
        this.alteracaoSono = alteracaoSono;
        this.alteracaoComportamento = alteracaoComportamento;
        this.observacao = observacao;
    }

    public String getCodigo() { return codigo; }
    public String getNome() { return nome; }
    public GrupoAlimento getGrupo() { return grupo; }
    public Boolean getAlergenico() { return alergenico; }
    public LocalDate getDataIntroducao() { return dataIntroducao; }
    public String getFormaPreparo() { return formaPreparo; }
    public TexturaAlimentar getTextura() { return textura; }
    public String getQuantidadeAproximada() { return quantidadeAproximada; }
    public AceitacaoAlimento getAceitacao() { return aceitacao; }
    public ClassificacaoGluten getClassificacaoGluten() { return classificacaoGluten; }
    public String getTipoPeixe() { return tipoPeixe; }
    public List<LocalDate> getDatasReexposicao() { return datasReexposicao; }
    public SituacaoSinaisOferta getSituacaoSinais() { return situacaoSinais; }
    public Boolean getRepetiuOutroDia() { return repetiuOutroDia; }
    public Boolean getSintomasPele() { return sintomasPele; }
    public Boolean getSintomasIntestinais() { return sintomasIntestinais; }
    public Boolean getSintomasRespiratorios() { return sintomasRespiratorios; }
    public Boolean getAlteracaoSono() { return alteracaoSono; }
    public Boolean getAlteracaoComportamento() { return alteracaoComportamento; }
    public String getObservacao() { return observacao; }
}
