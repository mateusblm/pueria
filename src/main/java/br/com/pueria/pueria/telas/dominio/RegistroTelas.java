package br.com.pueria.pueria.telas.dominio;

import br.com.pueria.pueria.comum.excecao.RegraDominioException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import java.util.List;

public class RegistroTelas {

    private final UUID id;
    private final UUID criancaId;
    private final LocalDate dataRegistro;
    private final Integer minutosDiaSemana;
    private final Integer minutosFimSemana;
    private final TipoConteudoTela tipoConteudoPredominante;
    private final List<ContextoUsoTela> contextosUso;
    private final Boolean telaAoAcordar;
    private final Boolean telaDuranteRefeicoes;
    private final Boolean telaAntesDormir;
    private final Boolean telaParaAcalmar;
    private final Boolean telaEmSegundoPlano;
    private final Boolean usoAcompanhadoAdulto;
    private final Boolean conteudoAdultoSupervisionado;
    private final Boolean criancaEscolheConteudoLivremente;
    private final Boolean videochamadaFamilia;
    private final Boolean autoplayAtivo;
    private final Boolean notificacoesAtivas;
    private final Boolean dispositivoNoQuarto;
    private final Boolean brincaAoArLivre;
    private final Boolean leituraBrincadeiraSemTela;
    private final Boolean preocupacaoFamilia;
    private final String observacao;
    private final LocalDateTime criadoEm;
    private final LocalDateTime atualizadoEm;

    private RegistroTelas(UUID id, UUID criancaId, DadosTelas dados, LocalDateTime criadoEm, LocalDateTime atualizadoEm) {
        this.id = Objects.requireNonNull(id, "O identificador do registro de telas e obrigatorio.");
        this.criancaId = Objects.requireNonNull(criancaId, "A crianca e obrigatoria.");
        this.dataRegistro = validarData(dados.dataRegistro());
        this.minutosDiaSemana = validarMinutos(dados.minutosDiaSemana(), "tempo em dias de semana");
        this.minutosFimSemana = validarMinutos(dados.minutosFimSemana(), "tempo em fim de semana");
        this.tipoConteudoPredominante = dados.tipoConteudoPredominante() == null
                ? TipoConteudoTela.NAO_INFORMADO
                : dados.tipoConteudoPredominante();
        this.contextosUso = dados.contextosUso() == null ? List.of() : dados.contextosUso().stream()
                .filter(Objects::nonNull)
                .collect(java.util.stream.Collectors.toMap(ContextoUsoTela::dispositivo, contexto -> contexto, (primeiro, ignorado) -> primeiro))
                .values().stream().toList();
        this.telaAoAcordar = dados.telaAoAcordar();
        this.telaDuranteRefeicoes = dados.telaDuranteRefeicoes();
        this.telaAntesDormir = dados.telaAntesDormir();
        this.telaParaAcalmar = dados.telaParaAcalmar();
        this.telaEmSegundoPlano = dados.telaEmSegundoPlano();
        this.usoAcompanhadoAdulto = dados.usoAcompanhadoAdulto();
        this.conteudoAdultoSupervisionado = dados.conteudoAdultoSupervisionado();
        this.criancaEscolheConteudoLivremente = dados.criancaEscolheConteudoLivremente();
        this.videochamadaFamilia = dados.videochamadaFamilia();
        this.autoplayAtivo = dados.autoplayAtivo();
        this.notificacoesAtivas = dados.notificacoesAtivas();
        this.dispositivoNoQuarto = dados.dispositivoNoQuarto();
        this.brincaAoArLivre = dados.brincaAoArLivre();
        this.leituraBrincadeiraSemTela = dados.leituraBrincadeiraSemTela();
        this.preocupacaoFamilia = dados.preocupacaoFamilia();
        this.observacao = tratarObservacao(dados.observacao());
        this.criadoEm = Objects.requireNonNull(criadoEm, "A data de criacao e obrigatoria.");
        this.atualizadoEm = atualizadoEm;
    }

    public static RegistroTelas registrar(UUID criancaId, DadosTelas dados) {
        return new RegistroTelas(UUID.randomUUID(), criancaId, dados, LocalDateTime.now(), null);
    }

    public static RegistroTelas restaurar(UUID id, UUID criancaId, DadosTelas dados, LocalDateTime criadoEm, LocalDateTime atualizadoEm) {
        return new RegistroTelas(id, criancaId, dados, criadoEm, atualizadoEm);
    }

    public RegistroTelas atualizar(DadosTelas dados) {
        return new RegistroTelas(id, criancaId, dados, criadoEm, LocalDateTime.now());
    }

    public Integer minutosMediosDia() {
        if (minutosDiaSemana == null && minutosFimSemana == null) {
            return null;
        }
        if (minutosDiaSemana == null) {
            return minutosFimSemana;
        }
        if (minutosFimSemana == null) {
            return minutosDiaSemana;
        }
        return Math.round((minutosDiaSemana * 5 + minutosFimSemana * 2) / 7.0f);
    }

    private static LocalDate validarData(LocalDate dataRegistro) {
        if (dataRegistro == null) {
            throw new RegraDominioException("A data do registro de telas e obrigatoria.");
        }
        if (dataRegistro.isAfter(LocalDate.now())) {
            throw new RegraDominioException("A data do registro de telas nao pode estar no futuro.");
        }
        return dataRegistro;
    }

    private static Integer validarMinutos(Integer valor, String nome) {
        if (valor == null) {
            return null;
        }
        if (valor < 0 || valor > 1440) {
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
    public Integer getMinutosDiaSemana() { return minutosDiaSemana; }
    public Integer getMinutosFimSemana() { return minutosFimSemana; }
    public TipoConteudoTela getTipoConteudoPredominante() { return tipoConteudoPredominante; }
    public List<ContextoUsoTela> getContextosUso() { return contextosUso; }
    public Boolean getTelaAoAcordar() { return telaAoAcordar; }
    public Boolean getTelaDuranteRefeicoes() { return telaDuranteRefeicoes; }
    public Boolean getTelaAntesDormir() { return telaAntesDormir; }
    public Boolean getTelaParaAcalmar() { return telaParaAcalmar; }
    public Boolean getTelaEmSegundoPlano() { return telaEmSegundoPlano; }
    public Boolean getUsoAcompanhadoAdulto() { return usoAcompanhadoAdulto; }
    public Boolean getConteudoAdultoSupervisionado() { return conteudoAdultoSupervisionado; }
    public Boolean getCriancaEscolheConteudoLivremente() { return criancaEscolheConteudoLivremente; }
    public Boolean getVideochamadaFamilia() { return videochamadaFamilia; }
    public Boolean getAutoplayAtivo() { return autoplayAtivo; }
    public Boolean getNotificacoesAtivas() { return notificacoesAtivas; }
    public Boolean getDispositivoNoQuarto() { return dispositivoNoQuarto; }
    public Boolean getBrincaAoArLivre() { return brincaAoArLivre; }
    public Boolean getLeituraBrincadeiraSemTela() { return leituraBrincadeiraSemTela; }
    public Boolean getPreocupacaoFamilia() { return preocupacaoFamilia; }
    public String getObservacao() { return observacao; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
}
