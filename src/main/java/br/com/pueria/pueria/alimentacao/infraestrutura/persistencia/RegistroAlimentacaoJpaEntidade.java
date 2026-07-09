package br.com.pueria.pueria.alimentacao.infraestrutura.persistencia;

import br.com.pueria.pueria.alimentacao.dominio.EstagioAlimentar;
import br.com.pueria.pueria.alimentacao.dominio.TexturaAlimentar;
import br.com.pueria.pueria.alimentacao.dominio.TipoLeiteAlimentacao;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "registros_alimentacao")
public class RegistroAlimentacaoJpaEntidade {

    @Id
    private UUID id;

    @Column(name = "crianca_id", nullable = false)
    private UUID criancaId;

    @Column(name = "data_registro", nullable = false)
    private LocalDate dataRegistro;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_leite", nullable = false, length = 30)
    private TipoLeiteAlimentacao tipoLeite;

    @Enumerated(EnumType.STRING)
    @Column(name = "estagio_alimentar", nullable = false, length = 50)
    private EstagioAlimentar estagioAlimentar;

    @Column(name = "idade_inicio_alimentacao_complementar_meses")
    private Integer idadeInicioAlimentacaoComplementarMeses;

    @Column(name = "refeicoes_por_dia")
    private Integer refeicoesPorDia;

    private Boolean consomeAgua;
    private Boolean usaMamadeira;
    private Boolean usaCopo;
    private Boolean usaColher;
    private Boolean blwMisto;
    private Boolean autoalimentacao;

    @Enumerated(EnumType.STRING)
    @Column(name = "textura_predominante", nullable = false, length = 30)
    private TexturaAlimentar texturaPredominante;

    private Boolean consomeFrutas;
    private Boolean consomeLegumesVerduras;
    private Boolean consomeLegumes;
    private Boolean consomeVerduras;
    private Boolean consomeCereaisTuberculos;
    private Boolean consomeFeijoesLeguminosas;
    private Boolean consomeCarnesOvos;
    private Boolean ultraprocessadosFrequentes;
    private Boolean bebidasAdocadas;
    private Boolean acucarAdicionado;
    private Boolean salAdicionado;
    private Boolean telasDuranteRefeicoes;
    private Boolean refeicoesEmFamilia;
    private Boolean rotinaAlimentarRegular;
    private Boolean seletividadeAlimentar;
    private Boolean recusaPersistente;
    private Boolean engasgosFrequentes;
    private Boolean vomitosRecorrentes;
    private Boolean constipacao;
    private Boolean diarreiaRecorrente;
    private Boolean dificuldadeGanhoPesoPercebida;
    private Boolean familiaTranquilaGanhoPesoAtual;
    private Boolean preocupacaoFamilia;

    @Column(length = 1000)
    private String observacao;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "registros_alimentacao_alimentos", joinColumns = @JoinColumn(name = "registro_alimentacao_id"))
    @OrderBy("grupo ASC, nome ASC")
    private List<AlimentoRegistroAlimentacaoJpaEmbeddable> alimentosOferecidos = new ArrayList<>();

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    protected RegistroAlimentacaoJpaEntidade() {}

    public RegistroAlimentacaoJpaEntidade(UUID id, UUID criancaId, LocalDate dataRegistro, TipoLeiteAlimentacao tipoLeite, EstagioAlimentar estagioAlimentar, Integer idadeInicioAlimentacaoComplementarMeses, Integer refeicoesPorDia, Boolean consomeAgua, Boolean usaMamadeira, Boolean usaCopo, Boolean usaColher, Boolean blwMisto, Boolean autoalimentacao, TexturaAlimentar texturaPredominante, Boolean consomeFrutas, Boolean consomeLegumesVerduras, Boolean consomeLegumes, Boolean consomeVerduras, Boolean consomeCereaisTuberculos, Boolean consomeFeijoesLeguminosas, Boolean consomeCarnesOvos, Boolean ultraprocessadosFrequentes, Boolean bebidasAdocadas, Boolean acucarAdicionado, Boolean salAdicionado, Boolean telasDuranteRefeicoes, Boolean refeicoesEmFamilia, Boolean rotinaAlimentarRegular, Boolean seletividadeAlimentar, Boolean recusaPersistente, Boolean engasgosFrequentes, Boolean vomitosRecorrentes, Boolean constipacao, Boolean diarreiaRecorrente, Boolean dificuldadeGanhoPesoPercebida, Boolean familiaTranquilaGanhoPesoAtual, Boolean preocupacaoFamilia, String observacao, List<AlimentoRegistroAlimentacaoJpaEmbeddable> alimentosOferecidos, LocalDateTime criadoEm, LocalDateTime atualizadoEm) {
        this.id = id;
        this.criancaId = criancaId;
        this.dataRegistro = dataRegistro;
        this.tipoLeite = tipoLeite;
        this.estagioAlimentar = estagioAlimentar;
        this.idadeInicioAlimentacaoComplementarMeses = idadeInicioAlimentacaoComplementarMeses;
        this.refeicoesPorDia = refeicoesPorDia;
        this.consomeAgua = consomeAgua;
        this.usaMamadeira = usaMamadeira;
        this.usaCopo = usaCopo;
        this.usaColher = usaColher;
        this.blwMisto = blwMisto;
        this.autoalimentacao = autoalimentacao;
        this.texturaPredominante = texturaPredominante;
        this.consomeFrutas = consomeFrutas;
        this.consomeLegumesVerduras = consomeLegumesVerduras;
        this.consomeLegumes = consomeLegumes;
        this.consomeVerduras = consomeVerduras;
        this.consomeCereaisTuberculos = consomeCereaisTuberculos;
        this.consomeFeijoesLeguminosas = consomeFeijoesLeguminosas;
        this.consomeCarnesOvos = consomeCarnesOvos;
        this.ultraprocessadosFrequentes = ultraprocessadosFrequentes;
        this.bebidasAdocadas = bebidasAdocadas;
        this.acucarAdicionado = acucarAdicionado;
        this.salAdicionado = salAdicionado;
        this.telasDuranteRefeicoes = telasDuranteRefeicoes;
        this.refeicoesEmFamilia = refeicoesEmFamilia;
        this.rotinaAlimentarRegular = rotinaAlimentarRegular;
        this.seletividadeAlimentar = seletividadeAlimentar;
        this.recusaPersistente = recusaPersistente;
        this.engasgosFrequentes = engasgosFrequentes;
        this.vomitosRecorrentes = vomitosRecorrentes;
        this.constipacao = constipacao;
        this.diarreiaRecorrente = diarreiaRecorrente;
        this.dificuldadeGanhoPesoPercebida = dificuldadeGanhoPesoPercebida;
        this.familiaTranquilaGanhoPesoAtual = familiaTranquilaGanhoPesoAtual;
        this.preocupacaoFamilia = preocupacaoFamilia;
        this.observacao = observacao;
        this.alimentosOferecidos = alimentosOferecidos == null ? new ArrayList<>() : new ArrayList<>(alimentosOferecidos);
        this.criadoEm = criadoEm;
        this.atualizadoEm = atualizadoEm;
    }

    public UUID getId() { return id; }
    public UUID getCriancaId() { return criancaId; }
    public LocalDate getDataRegistro() { return dataRegistro; }
    public TipoLeiteAlimentacao getTipoLeite() { return tipoLeite; }
    public EstagioAlimentar getEstagioAlimentar() { return estagioAlimentar; }
    public Integer getIdadeInicioAlimentacaoComplementarMeses() { return idadeInicioAlimentacaoComplementarMeses; }
    public Integer getRefeicoesPorDia() { return refeicoesPorDia; }
    public Boolean getConsomeAgua() { return consomeAgua; }
    public Boolean getUsaMamadeira() { return usaMamadeira; }
    public Boolean getUsaCopo() { return usaCopo; }
    public Boolean getUsaColher() { return usaColher; }
    public Boolean getBlwMisto() { return blwMisto; }
    public Boolean getAutoalimentacao() { return autoalimentacao; }
    public TexturaAlimentar getTexturaPredominante() { return texturaPredominante; }
    public Boolean getConsomeFrutas() { return consomeFrutas; }
    public Boolean getConsomeLegumesVerduras() { return consomeLegumesVerduras; }
    public Boolean getConsomeLegumes() { return consomeLegumes; }
    public Boolean getConsomeVerduras() { return consomeVerduras; }
    public Boolean getConsomeCereaisTuberculos() { return consomeCereaisTuberculos; }
    public Boolean getConsomeFeijoesLeguminosas() { return consomeFeijoesLeguminosas; }
    public Boolean getConsomeCarnesOvos() { return consomeCarnesOvos; }
    public Boolean getUltraprocessadosFrequentes() { return ultraprocessadosFrequentes; }
    public Boolean getBebidasAdocadas() { return bebidasAdocadas; }
    public Boolean getAcucarAdicionado() { return acucarAdicionado; }
    public Boolean getSalAdicionado() { return salAdicionado; }
    public Boolean getTelasDuranteRefeicoes() { return telasDuranteRefeicoes; }
    public Boolean getRefeicoesEmFamilia() { return refeicoesEmFamilia; }
    public Boolean getRotinaAlimentarRegular() { return rotinaAlimentarRegular; }
    public Boolean getSeletividadeAlimentar() { return seletividadeAlimentar; }
    public Boolean getRecusaPersistente() { return recusaPersistente; }
    public Boolean getEngasgosFrequentes() { return engasgosFrequentes; }
    public Boolean getVomitosRecorrentes() { return vomitosRecorrentes; }
    public Boolean getConstipacao() { return constipacao; }
    public Boolean getDiarreiaRecorrente() { return diarreiaRecorrente; }
    public Boolean getDificuldadeGanhoPesoPercebida() { return dificuldadeGanhoPesoPercebida; }
    public Boolean getFamiliaTranquilaGanhoPesoAtual() { return familiaTranquilaGanhoPesoAtual; }
    public Boolean getPreocupacaoFamilia() { return preocupacaoFamilia; }
    public String getObservacao() { return observacao; }
    public List<AlimentoRegistroAlimentacaoJpaEmbeddable> getAlimentosOferecidos() { return alimentosOferecidos; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
}
