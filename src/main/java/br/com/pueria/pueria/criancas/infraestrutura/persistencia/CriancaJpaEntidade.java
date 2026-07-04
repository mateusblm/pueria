package br.com.pueria.pueria.criancas.infraestrutura.persistencia;

import br.com.pueria.pueria.criancas.dominio.Sexo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

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

    @Column(name = "semanas_gestacionais")
    private Integer semanasGestacionais;

    @Column(name = "peso_nascimento_gramas")
    private Integer pesoNascimentoGramas;

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

    public Integer getPesoNascimentoGramas() {
        return pesoNascimentoGramas;
    }

    public void setPesoNascimentoGramas(Integer pesoNascimentoGramas) {
        this.pesoNascimentoGramas = pesoNascimentoGramas;
    }

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
