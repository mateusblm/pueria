package br.com.pueria.pueria.transitointestinal.dominio;

import br.com.pueria.pueria.comum.excecao.RegraDominioException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class RegistroTransitoIntestinal {

    private final UUID id;
    private final UUID criancaId;
    private final LocalDate dataRegistro;
    private final TipoFezesBristol tipoFezes;
    private final Integer evacuacoesPorDia;
    private final FacilidadeLimpezaFezes facilidadeLimpeza;
    private final Boolean muco;
    private final Boolean restosAlimentares;
    private final Boolean raiasSangue;
    private final Boolean constipacao;
    private final Boolean diarreia;
    private final Boolean dorEvacuar;
    private final Boolean escapeFecal;
    private final Boolean assaduraFrequente;
    private final Boolean assaduraVermelhidao;
    private final Boolean assaduraPontosVermelhos;
    private final Boolean preocupacaoFamilia;
    private final String observacao;
    private final LocalDateTime criadoEm;
    private final LocalDateTime atualizadoEm;

    private RegistroTransitoIntestinal(UUID id, UUID criancaId, DadosTransitoIntestinal dados, LocalDateTime criadoEm, LocalDateTime atualizadoEm) {
        this.id = Objects.requireNonNull(id, "O identificador do registro intestinal e obrigatorio.");
        this.criancaId = Objects.requireNonNull(criancaId, "A crianca e obrigatoria.");
        this.dataRegistro = validarData(dados.dataRegistro());
        this.tipoFezes = dados.tipoFezes() == null ? TipoFezesBristol.NAO_INFORMADO : dados.tipoFezes();
        this.evacuacoesPorDia = validarInteiro(dados.evacuacoesPorDia(), 0, 30, "evacuacoes por dia");
        this.facilidadeLimpeza = dados.facilidadeLimpeza() == null ? FacilidadeLimpezaFezes.NAO_INFORMADO : dados.facilidadeLimpeza();
        this.muco = dados.muco();
        this.restosAlimentares = dados.restosAlimentares();
        this.raiasSangue = dados.raiasSangue();
        this.constipacao = dados.constipacao();
        this.diarreia = dados.diarreia();
        this.dorEvacuar = dados.dorEvacuar();
        this.escapeFecal = dados.escapeFecal();
        this.assaduraFrequente = dados.assaduraFrequente();
        this.assaduraVermelhidao = dados.assaduraVermelhidao();
        this.assaduraPontosVermelhos = dados.assaduraPontosVermelhos();
        this.preocupacaoFamilia = dados.preocupacaoFamilia();
        this.observacao = tratarObservacao(dados.observacao());
        this.criadoEm = Objects.requireNonNull(criadoEm, "A data de criacao e obrigatoria.");
        this.atualizadoEm = atualizadoEm;
    }

    public static RegistroTransitoIntestinal registrar(UUID criancaId, DadosTransitoIntestinal dados) {
        return new RegistroTransitoIntestinal(UUID.randomUUID(), criancaId, dados, LocalDateTime.now(), null);
    }

    public static RegistroTransitoIntestinal restaurar(UUID id, UUID criancaId, DadosTransitoIntestinal dados, LocalDateTime criadoEm, LocalDateTime atualizadoEm) {
        return new RegistroTransitoIntestinal(id, criancaId, dados, criadoEm, atualizadoEm);
    }

    public RegistroTransitoIntestinal atualizar(DadosTransitoIntestinal dados) {
        return new RegistroTransitoIntestinal(id, criancaId, dados, criadoEm, LocalDateTime.now());
    }

    private static LocalDate validarData(LocalDate dataRegistro) {
        if (dataRegistro == null) {
            throw new RegraDominioException("A data do registro intestinal e obrigatoria.");
        }
        if (dataRegistro.isAfter(LocalDate.now())) {
            throw new RegraDominioException("A data do registro intestinal nao pode estar no futuro.");
        }
        return dataRegistro;
    }

    private static Integer validarInteiro(Integer valor, int minimo, int maximo, String nome) {
        if (valor == null) {
            return null;
        }
        if (valor < minimo || valor > maximo) {
            throw new RegraDominioException("O campo " + nome + " esta fora do limite permitido.");
        }
        return valor;
    }

    private static String tratarObservacao(String observacao) {
        if (observacao == null || observacao.isBlank()) {
            return null;
        }
        String texto = observacao.trim().replaceAll("\\s+", " ");
        if (texto.length() > 1000) {
            throw new RegraDominioException("A observacao deve ter no maximo 1000 caracteres.");
        }
        return texto;
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
