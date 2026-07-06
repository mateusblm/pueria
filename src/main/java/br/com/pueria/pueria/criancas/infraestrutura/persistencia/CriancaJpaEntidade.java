package br.com.pueria.pueria.criancas.infraestrutura.persistencia;

import br.com.pueria.pueria.criancas.dominio.AlimentacaoInicial;
import br.com.pueria.pueria.criancas.dominio.Sexo;
import br.com.pueria.pueria.criancas.dominio.StatusTriagemNeonatal;
import br.com.pueria.pueria.criancas.dominio.TipoParto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "criancas")
public class CriancaJpaEntidade {

    @Id
    private UUID id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(name = "nome_normalizado", nullable = false, length = 150)
    private String nomeNormalizado;

    @Column(name = "data_nascimento", nullable = false)
    private LocalDate dataNascimento;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Sexo sexo;

    @Column(nullable = false)
    private boolean prematura;

    @Column(name = "semanas_gestacionais", nullable = false)
    private Integer semanasGestacionais;

    @Column(name = "dias_gestacionais", nullable = false)
    private Integer diasGestacionais;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_parto", nullable = false, length = 30)
    private TipoParto tipoParto;

    @Column(name = "peso_nascimento_gramas", nullable = false)
    private Integer pesoNascimentoGramas;

    @Column(name = "comprimento_nascimento_cm", nullable = false, precision = 5, scale = 2)
    private BigDecimal comprimentoNascimentoCm;

    @Column(name = "perimetro_cefalico_nascimento_cm", nullable = false, precision = 5, scale = 2)
    private BigDecimal perimetroCefalicoNascimentoCm;

    @Column(name = "apgar_um_minuto")
    private Integer apgarUmMinuto;

    @Column(name = "apgar_cinco_minutos")
    private Integer apgarCincoMinutos;

    @Column(name = "uti_neonatal", nullable = false)
    private boolean utiNeonatal;

    @Column(name = "reanimacao_neonatal", nullable = false)
    private boolean reanimacaoNeonatal;

    @Column(name = "ictericia_neonatal", nullable = false)
    private boolean ictericiaNeonatal;

    @Column(name = "dificuldade_respiratoria", nullable = false)
    private boolean dificuldadeRespiratoria;

    @Column(name = "dificuldade_amamentacao", nullable = false)
    private boolean dificuldadeAmamentacao;

    @Column(name = "observacoes_nascimento", length = 1000)
    private String observacoesNascimento;

    @Column(name = "pre_natal_realizado", nullable = false)
    private boolean preNatalRealizado;

    @Column(name = "consultas_pre_natal")
    private Integer consultasPreNatal;

    @Column(name = "diabetes_gestacional", nullable = false)
    private boolean diabetesGestacional;

    @Column(name = "hipertensao_gestacional", nullable = false)
    private boolean hipertensaoGestacional;

    @Column(name = "infeccao_gestacional", nullable = false)
    private boolean infeccaoGestacional;

    @Column(name = "sangramento_gestacional", nullable = false)
    private boolean sangramentoGestacional;

    @Column(name = "uso_alcool_gestacao", nullable = false)
    private boolean usoAlcoolGestacao;

    @Column(name = "uso_tabaco_gestacao", nullable = false)
    private boolean usoTabacoGestacao;

    @Column(name = "outras_exposicoes_gestacao", nullable = false)
    private boolean outrasExposicoesGestacao;

    @Column(name = "observacoes_gestacao", length = 1000)
    private String observacoesGestacao;

    @Column(name = "dias_alta_hospitalar")
    private Integer diasAltaHospitalar;

    @Column(name = "retorno_hospitalar_primeira_semana", nullable = false)
    private boolean retornoHospitalarPrimeiraSemana;

    @Enumerated(EnumType.STRING)
    @Column(name = "teste_pezinho", nullable = false, length = 20)
    private StatusTriagemNeonatal testePezinho;

    @Enumerated(EnumType.STRING)
    @Column(name = "teste_orelhinha", nullable = false, length = 20)
    private StatusTriagemNeonatal testeOrelhinha;

    @Enumerated(EnumType.STRING)
    @Column(name = "teste_olhinho", nullable = false, length = 20)
    private StatusTriagemNeonatal testeOlhinho;

    @Enumerated(EnumType.STRING)
    @Column(name = "teste_coracaozinho", nullable = false, length = 20)
    private StatusTriagemNeonatal testeCoracaozinho;

    @Column(name = "amamentacao_primeira_hora", nullable = false)
    private boolean amamentacaoPrimeiraHora;

    @Enumerated(EnumType.STRING)
    @Column(name = "alimentacao_inicial", nullable = false, length = 40)
    private AlimentacaoInicial alimentacaoInicial;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    protected CriancaJpaEntidade() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNomeNormalizado() {
        return nomeNormalizado;
    }

    public void setNomeNormalizado(String nomeNormalizado) {
        this.nomeNormalizado = nomeNormalizado;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public Sexo getSexo() {
        return sexo;
    }

    public void setSexo(Sexo sexo) {
        this.sexo = sexo;
    }

    public boolean isPrematura() {
        return prematura;
    }

    public void setPrematura(boolean prematura) {
        this.prematura = prematura;
    }

    public Integer getSemanasGestacionais() {
        return semanasGestacionais;
    }

    public void setSemanasGestacionais(Integer semanasGestacionais) {
        this.semanasGestacionais = semanasGestacionais;
    }

    public Integer getDiasGestacionais() {
        return diasGestacionais;
    }

    public void setDiasGestacionais(Integer diasGestacionais) {
        this.diasGestacionais = diasGestacionais;
    }

    public TipoParto getTipoParto() {
        return tipoParto;
    }

    public void setTipoParto(TipoParto tipoParto) {
        this.tipoParto = tipoParto;
    }

    public Integer getPesoNascimentoGramas() {
        return pesoNascimentoGramas;
    }

    public void setPesoNascimentoGramas(Integer pesoNascimentoGramas) {
        this.pesoNascimentoGramas = pesoNascimentoGramas;
    }

    public BigDecimal getComprimentoNascimentoCm() {
        return comprimentoNascimentoCm;
    }

    public void setComprimentoNascimentoCm(BigDecimal comprimentoNascimentoCm) {
        this.comprimentoNascimentoCm = comprimentoNascimentoCm;
    }

    public BigDecimal getPerimetroCefalicoNascimentoCm() {
        return perimetroCefalicoNascimentoCm;
    }

    public void setPerimetroCefalicoNascimentoCm(BigDecimal perimetroCefalicoNascimentoCm) {
        this.perimetroCefalicoNascimentoCm = perimetroCefalicoNascimentoCm;
    }

    public Integer getApgarUmMinuto() {
        return apgarUmMinuto;
    }

    public void setApgarUmMinuto(Integer apgarUmMinuto) {
        this.apgarUmMinuto = apgarUmMinuto;
    }

    public Integer getApgarCincoMinutos() {
        return apgarCincoMinutos;
    }

    public void setApgarCincoMinutos(Integer apgarCincoMinutos) {
        this.apgarCincoMinutos = apgarCincoMinutos;
    }

    public boolean isUtiNeonatal() {
        return utiNeonatal;
    }

    public void setUtiNeonatal(boolean utiNeonatal) {
        this.utiNeonatal = utiNeonatal;
    }

    public boolean isReanimacaoNeonatal() {
        return reanimacaoNeonatal;
    }

    public void setReanimacaoNeonatal(boolean reanimacaoNeonatal) {
        this.reanimacaoNeonatal = reanimacaoNeonatal;
    }

    public boolean isIctericiaNeonatal() {
        return ictericiaNeonatal;
    }

    public void setIctericiaNeonatal(boolean ictericiaNeonatal) {
        this.ictericiaNeonatal = ictericiaNeonatal;
    }

    public boolean isDificuldadeRespiratoria() {
        return dificuldadeRespiratoria;
    }

    public void setDificuldadeRespiratoria(boolean dificuldadeRespiratoria) {
        this.dificuldadeRespiratoria = dificuldadeRespiratoria;
    }

    public boolean isDificuldadeAmamentacao() {
        return dificuldadeAmamentacao;
    }

    public void setDificuldadeAmamentacao(boolean dificuldadeAmamentacao) {
        this.dificuldadeAmamentacao = dificuldadeAmamentacao;
    }

    public String getObservacoesNascimento() {
        return observacoesNascimento;
    }

    public void setObservacoesNascimento(String observacoesNascimento) {
        this.observacoesNascimento = observacoesNascimento;
    }

    public boolean isPreNatalRealizado() { return preNatalRealizado; }
    public void setPreNatalRealizado(boolean preNatalRealizado) { this.preNatalRealizado = preNatalRealizado; }
    public Integer getConsultasPreNatal() { return consultasPreNatal; }
    public void setConsultasPreNatal(Integer consultasPreNatal) { this.consultasPreNatal = consultasPreNatal; }
    public boolean isDiabetesGestacional() { return diabetesGestacional; }
    public void setDiabetesGestacional(boolean diabetesGestacional) { this.diabetesGestacional = diabetesGestacional; }
    public boolean isHipertensaoGestacional() { return hipertensaoGestacional; }
    public void setHipertensaoGestacional(boolean hipertensaoGestacional) { this.hipertensaoGestacional = hipertensaoGestacional; }
    public boolean isInfeccaoGestacional() { return infeccaoGestacional; }
    public void setInfeccaoGestacional(boolean infeccaoGestacional) { this.infeccaoGestacional = infeccaoGestacional; }
    public boolean isSangramentoGestacional() { return sangramentoGestacional; }
    public void setSangramentoGestacional(boolean sangramentoGestacional) { this.sangramentoGestacional = sangramentoGestacional; }
    public boolean isUsoAlcoolGestacao() { return usoAlcoolGestacao; }
    public void setUsoAlcoolGestacao(boolean usoAlcoolGestacao) { this.usoAlcoolGestacao = usoAlcoolGestacao; }
    public boolean isUsoTabacoGestacao() { return usoTabacoGestacao; }
    public void setUsoTabacoGestacao(boolean usoTabacoGestacao) { this.usoTabacoGestacao = usoTabacoGestacao; }
    public boolean isOutrasExposicoesGestacao() { return outrasExposicoesGestacao; }
    public void setOutrasExposicoesGestacao(boolean outrasExposicoesGestacao) { this.outrasExposicoesGestacao = outrasExposicoesGestacao; }
    public String getObservacoesGestacao() { return observacoesGestacao; }
    public void setObservacoesGestacao(String observacoesGestacao) { this.observacoesGestacao = observacoesGestacao; }
    public Integer getDiasAltaHospitalar() { return diasAltaHospitalar; }
    public void setDiasAltaHospitalar(Integer diasAltaHospitalar) { this.diasAltaHospitalar = diasAltaHospitalar; }
    public boolean isRetornoHospitalarPrimeiraSemana() { return retornoHospitalarPrimeiraSemana; }
    public void setRetornoHospitalarPrimeiraSemana(boolean retornoHospitalarPrimeiraSemana) { this.retornoHospitalarPrimeiraSemana = retornoHospitalarPrimeiraSemana; }
    public StatusTriagemNeonatal getTestePezinho() { return testePezinho; }
    public void setTestePezinho(StatusTriagemNeonatal testePezinho) { this.testePezinho = testePezinho; }
    public StatusTriagemNeonatal getTesteOrelhinha() { return testeOrelhinha; }
    public void setTesteOrelhinha(StatusTriagemNeonatal testeOrelhinha) { this.testeOrelhinha = testeOrelhinha; }
    public StatusTriagemNeonatal getTesteOlhinho() { return testeOlhinho; }
    public void setTesteOlhinho(StatusTriagemNeonatal testeOlhinho) { this.testeOlhinho = testeOlhinho; }
    public StatusTriagemNeonatal getTesteCoracaozinho() { return testeCoracaozinho; }
    public void setTesteCoracaozinho(StatusTriagemNeonatal testeCoracaozinho) { this.testeCoracaozinho = testeCoracaozinho; }
    public boolean isAmamentacaoPrimeiraHora() { return amamentacaoPrimeiraHora; }
    public void setAmamentacaoPrimeiraHora(boolean amamentacaoPrimeiraHora) { this.amamentacaoPrimeiraHora = amamentacaoPrimeiraHora; }
    public AlimentacaoInicial getAlimentacaoInicial() { return alimentacaoInicial; }
    public void setAlimentacaoInicial(AlimentacaoInicial alimentacaoInicial) { this.alimentacaoInicial = alimentacaoInicial; }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    public void setAtualizadoEm(LocalDateTime atualizadoEm) {
        this.atualizadoEm = atualizadoEm;
    }
}
