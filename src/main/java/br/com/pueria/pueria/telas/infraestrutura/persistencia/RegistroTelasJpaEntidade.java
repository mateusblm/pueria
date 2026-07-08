package br.com.pueria.pueria.telas.infraestrutura.persistencia;

import br.com.pueria.pueria.telas.dominio.TipoConteudoTela;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "registros_telas")
public class RegistroTelasJpaEntidade {

    @Id
    private UUID id;

    @Column(name = "crianca_id", nullable = false)
    private UUID criancaId;

    @Column(name = "data_registro", nullable = false)
    private LocalDate dataRegistro;

    private Integer minutosDiaSemana;
    private Integer minutosFimSemana;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_conteudo_predominante", nullable = false, length = 40)
    private TipoConteudoTela tipoConteudoPredominante;

    private Boolean telaAoAcordar;
    private Boolean telaDuranteRefeicoes;
    private Boolean telaAntesDormir;
    private Boolean telaParaAcalmar;
    private Boolean telaEmSegundoPlano;
    private Boolean usoAcompanhadoAdulto;
    private Boolean conteudoAdultoSupervisionado;
    private Boolean videochamadaFamilia;
    private Boolean autoplayAtivo;
    private Boolean notificacoesAtivas;
    private Boolean dispositivoNoQuarto;
    private Boolean brincaAoArLivre;
    private Boolean leituraBrincadeiraSemTela;
    private Boolean preocupacaoFamilia;

    @Column(length = 1000)
    private String observacao;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    protected RegistroTelasJpaEntidade() {}

    public RegistroTelasJpaEntidade(UUID id, UUID criancaId, LocalDate dataRegistro, Integer minutosDiaSemana, Integer minutosFimSemana, TipoConteudoTela tipoConteudoPredominante, Boolean telaAoAcordar, Boolean telaDuranteRefeicoes, Boolean telaAntesDormir, Boolean telaParaAcalmar, Boolean telaEmSegundoPlano, Boolean usoAcompanhadoAdulto, Boolean conteudoAdultoSupervisionado, Boolean videochamadaFamilia, Boolean autoplayAtivo, Boolean notificacoesAtivas, Boolean dispositivoNoQuarto, Boolean brincaAoArLivre, Boolean leituraBrincadeiraSemTela, Boolean preocupacaoFamilia, String observacao, LocalDateTime criadoEm, LocalDateTime atualizadoEm) {
        this.id = id;
        this.criancaId = criancaId;
        this.dataRegistro = dataRegistro;
        this.minutosDiaSemana = minutosDiaSemana;
        this.minutosFimSemana = minutosFimSemana;
        this.tipoConteudoPredominante = tipoConteudoPredominante;
        this.telaAoAcordar = telaAoAcordar;
        this.telaDuranteRefeicoes = telaDuranteRefeicoes;
        this.telaAntesDormir = telaAntesDormir;
        this.telaParaAcalmar = telaParaAcalmar;
        this.telaEmSegundoPlano = telaEmSegundoPlano;
        this.usoAcompanhadoAdulto = usoAcompanhadoAdulto;
        this.conteudoAdultoSupervisionado = conteudoAdultoSupervisionado;
        this.videochamadaFamilia = videochamadaFamilia;
        this.autoplayAtivo = autoplayAtivo;
        this.notificacoesAtivas = notificacoesAtivas;
        this.dispositivoNoQuarto = dispositivoNoQuarto;
        this.brincaAoArLivre = brincaAoArLivre;
        this.leituraBrincadeiraSemTela = leituraBrincadeiraSemTela;
        this.preocupacaoFamilia = preocupacaoFamilia;
        this.observacao = observacao;
        this.criadoEm = criadoEm;
        this.atualizadoEm = atualizadoEm;
    }

    public UUID getId() { return id; }
    public UUID getCriancaId() { return criancaId; }
    public LocalDate getDataRegistro() { return dataRegistro; }
    public Integer getMinutosDiaSemana() { return minutosDiaSemana; }
    public Integer getMinutosFimSemana() { return minutosFimSemana; }
    public TipoConteudoTela getTipoConteudoPredominante() { return tipoConteudoPredominante; }
    public Boolean getTelaAoAcordar() { return telaAoAcordar; }
    public Boolean getTelaDuranteRefeicoes() { return telaDuranteRefeicoes; }
    public Boolean getTelaAntesDormir() { return telaAntesDormir; }
    public Boolean getTelaParaAcalmar() { return telaParaAcalmar; }
    public Boolean getTelaEmSegundoPlano() { return telaEmSegundoPlano; }
    public Boolean getUsoAcompanhadoAdulto() { return usoAcompanhadoAdulto; }
    public Boolean getConteudoAdultoSupervisionado() { return conteudoAdultoSupervisionado; }
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
