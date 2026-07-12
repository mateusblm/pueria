package br.com.pueria.pueria.desenvolvimento.infraestrutura.persistencia;

import br.com.pueria.pueria.desenvolvimento.dominio.AreaDesenvolvimento;
import jakarta.persistence.*;
import java.util.UUID;

@Entity @Table(name = "estimulos_desenvolvimento")
public class EstimuloDesenvolvimentoJpaEntidade {
    @Id private UUID id;
    @Column(name = "idade_inicial_meses", nullable = false) private int idadeInicialMeses;
    @Column(name = "idade_final_meses", nullable = false) private int idadeFinalMeses;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 40) private AreaDesenvolvimento area;
    @Column(nullable = false, length = 140) private String titulo;
    @Column(nullable = false, length = 800) private String descricao;
    @Column(nullable = false, length = 500) private String cuidado;
    @Column(nullable = false, length = 180) private String fonte;
    @Column(name = "versao_catalogo", nullable = false, length = 40) private String versaoCatalogo;
    @Column(nullable = false) private boolean ativo;
    protected EstimuloDesenvolvimentoJpaEntidade() { }
    public EstimuloDesenvolvimentoJpaEntidade(UUID id, int inicio, int fim, AreaDesenvolvimento area, String titulo, String descricao, String cuidado, String fonte, String versao, boolean ativo) { this.id=id; this.idadeInicialMeses=inicio; this.idadeFinalMeses=fim; this.area=area; this.titulo=titulo; this.descricao=descricao; this.cuidado=cuidado; this.fonte=fonte; this.versaoCatalogo=versao; this.ativo=ativo; }
    public UUID getId(){return id;} public int getIdadeInicialMeses(){return idadeInicialMeses;} public int getIdadeFinalMeses(){return idadeFinalMeses;} public AreaDesenvolvimento getArea(){return area;} public String getTitulo(){return titulo;} public String getDescricao(){return descricao;} public String getCuidado(){return cuidado;} public String getFonte(){return fonte;} public String getVersaoCatalogo(){return versaoCatalogo;} public boolean isAtivo(){return ativo;}
}
