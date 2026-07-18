package br.com.pueria.pueria.sono.infraestrutura.persistencia;

import br.com.pueria.pueria.sono.dominio.*;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;
import java.util.List;

@Entity
@Table(name = "registros_sono")
public class RegistroSonoJpaEntidade {

    @Id
    private UUID id;

    @Column(name = "crianca_id", nullable = false)
    private UUID criancaId;

    @Column(name = "data_registro", nullable = false)
    private LocalDate dataRegistro;

    private LocalTime horarioDormiu;
    private LocalTime horarioAcordou;
    private Integer quantidadeCochilos;
    private Integer minutosCochilos;
    private Integer despertaresNoturnos;
    private Boolean dificuldadeIniciarSono;
    private Boolean rotinaSonoConsistente;
    private Boolean telasAntesDormir;

    @Enumerated(EnumType.STRING)
    @Column(name = "superficie_sono", nullable = false, length = 30)
    private SuperficieSono superficieSono;

    @Enumerated(EnumType.STRING)
    @Column(name = "ambiente_sono", nullable = false, length = 35)
    private AmbienteSono ambienteSono;

    @ElementCollection(targetClass = TipoDespertarNoturno.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "registros_sono_tipos_despertar", joinColumns = @JoinColumn(name = "registro_sono_id"))
    @Column(name = "tipo_despertar", nullable = false, length = 45)
    @Enumerated(EnumType.STRING)
    private List<TipoDespertarNoturno> tiposDespertarNoturno;

    private Boolean roncosFrequentes;
    private Boolean pausasRespiratoriasPercebidas;
    private Boolean sonoAgitado;
    private Boolean rangerDentesDuranteSono;
    private Boolean acordaBemDisposto;
    private Boolean sonolenciaDiurna;
    private Boolean irritabilidadeCansaco;
    private Boolean dificilDeSerAcordado;
    private Boolean malHumorado;
    private Boolean irritado;
    private Boolean preocupacaoFamilia;

    @Column(length = 1000)
    private String observacao;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    protected RegistroSonoJpaEntidade() {}

    public RegistroSonoJpaEntidade(UUID id, UUID criancaId, LocalDate dataRegistro, LocalTime horarioDormiu, LocalTime horarioAcordou, Integer quantidadeCochilos, Integer minutosCochilos, Integer despertaresNoturnos, Boolean dificuldadeIniciarSono, Boolean rotinaSonoConsistente, Boolean telasAntesDormir, SuperficieSono superficieSono, AmbienteSono ambienteSono, List<TipoDespertarNoturno> tiposDespertarNoturno, Boolean roncosFrequentes, Boolean pausasRespiratoriasPercebidas, Boolean sonoAgitado, Boolean rangerDentesDuranteSono, Boolean acordaBemDisposto, Boolean sonolenciaDiurna, Boolean irritabilidadeCansaco, Boolean dificilDeSerAcordado, Boolean malHumorado, Boolean irritado, Boolean preocupacaoFamilia, String observacao, LocalDateTime criadoEm, LocalDateTime atualizadoEm) {
        this.id = id;
        this.criancaId = criancaId;
        this.dataRegistro = dataRegistro;
        this.horarioDormiu = horarioDormiu;
        this.horarioAcordou = horarioAcordou;
        this.quantidadeCochilos = quantidadeCochilos;
        this.minutosCochilos = minutosCochilos;
        this.despertaresNoturnos = despertaresNoturnos;
        this.dificuldadeIniciarSono = dificuldadeIniciarSono;
        this.rotinaSonoConsistente = rotinaSonoConsistente;
        this.telasAntesDormir = telasAntesDormir;
        this.superficieSono = superficieSono;
        this.ambienteSono = ambienteSono;
        this.tiposDespertarNoturno = tiposDespertarNoturno;
        this.roncosFrequentes = roncosFrequentes;
        this.pausasRespiratoriasPercebidas = pausasRespiratoriasPercebidas;
        this.sonoAgitado = sonoAgitado;
        this.rangerDentesDuranteSono = rangerDentesDuranteSono;
        this.acordaBemDisposto = acordaBemDisposto;
        this.sonolenciaDiurna = sonolenciaDiurna;
        this.irritabilidadeCansaco = irritabilidadeCansaco;
        this.dificilDeSerAcordado = dificilDeSerAcordado;
        this.malHumorado = malHumorado;
        this.irritado = irritado;
        this.preocupacaoFamilia = preocupacaoFamilia;
        this.observacao = observacao;
        this.criadoEm = criadoEm;
        this.atualizadoEm = atualizadoEm;
    }

    public UUID getId() { return id; }
    public UUID getCriancaId() { return criancaId; }
    public LocalDate getDataRegistro() { return dataRegistro; }
    public LocalTime getHorarioDormiu() { return horarioDormiu; }
    public LocalTime getHorarioAcordou() { return horarioAcordou; }
    public Integer getQuantidadeCochilos() { return quantidadeCochilos; }
    public Integer getMinutosCochilos() { return minutosCochilos; }
    public Integer getDespertaresNoturnos() { return despertaresNoturnos; }
    public Boolean getDificuldadeIniciarSono() { return dificuldadeIniciarSono; }
    public Boolean getRotinaSonoConsistente() { return rotinaSonoConsistente; }
    public Boolean getTelasAntesDormir() { return telasAntesDormir; }
    public SuperficieSono getSuperficieSono() { return superficieSono; }
    public AmbienteSono getAmbienteSono() { return ambienteSono; }
    public List<TipoDespertarNoturno> getTiposDespertarNoturno() { return tiposDespertarNoturno; }
    public Boolean getRoncosFrequentes() { return roncosFrequentes; }
    public Boolean getPausasRespiratoriasPercebidas() { return pausasRespiratoriasPercebidas; }
    public Boolean getSonoAgitado() { return sonoAgitado; }
    public Boolean getRangerDentesDuranteSono() { return rangerDentesDuranteSono; }
    public Boolean getAcordaBemDisposto() { return acordaBemDisposto; }
    public Boolean getSonolenciaDiurna() { return sonolenciaDiurna; }
    public Boolean getIrritabilidadeCansaco() { return irritabilidadeCansaco; }
    public Boolean getDificilDeSerAcordado() { return dificilDeSerAcordado; }
    public Boolean getMalHumorado() { return malHumorado; }
    public Boolean getIrritado() { return irritado; }
    public Boolean getPreocupacaoFamilia() { return preocupacaoFamilia; }
    public String getObservacao() { return observacao; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
}
