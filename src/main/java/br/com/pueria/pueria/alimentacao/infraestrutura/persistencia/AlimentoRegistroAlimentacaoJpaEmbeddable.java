package br.com.pueria.pueria.alimentacao.infraestrutura.persistencia;

import br.com.pueria.pueria.alimentacao.dominio.GrupoAlimento;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Embeddable
public class AlimentoRegistroAlimentacaoJpaEmbeddable {

    @Column(name = "codigo", nullable = false, length = 80)
    private String codigo;

    @Column(name = "nome", nullable = false, length = 120)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(name = "grupo", nullable = false, length = 40)
    private GrupoAlimento grupo;

    protected AlimentoRegistroAlimentacaoJpaEmbeddable() {}

    public AlimentoRegistroAlimentacaoJpaEmbeddable(String codigo, String nome, GrupoAlimento grupo) {
        this.codigo = codigo;
        this.nome = nome;
        this.grupo = grupo;
    }

    public String getCodigo() { return codigo; }
    public String getNome() { return nome; }
    public GrupoAlimento getGrupo() { return grupo; }
}
