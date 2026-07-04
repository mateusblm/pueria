package br.com.pueria.pueria.desenvolvimento.infraestrutura.persistencia;

import br.com.pueria.pueria.desenvolvimento.dominio.AreaDesenvolvimento;
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

    @Column(nullable = false)
    private boolean ativo;

    protected MarcoDesenvolvimentoJpaEntidade() {
    }

    public UUID getId() { return id; }
    public int getIdadeMeses() { return idadeMeses; }
    public AreaDesenvolvimento getArea() { return area; }
    public String getDescricao() { return descricao; }
    public String getFonte() { return fonte; }
    public boolean isAtivo() { return ativo; }
}
