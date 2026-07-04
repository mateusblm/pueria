package br.com.pueria.pueria.desenvolvimento.dominio;

import br.com.pueria.pueria.comum.excecao.RegraDominioException;

import java.util.UUID;

public class MarcoDesenvolvimento {

    private final UUID id;
    private final int idadeMeses;
    private final AreaDesenvolvimento area;
    private final String descricao;
    private final String fonte;
    private final boolean ativo;

    private MarcoDesenvolvimento(UUID id, int idadeMeses, AreaDesenvolvimento area, String descricao, String fonte, boolean ativo) {
        if (id == null) {
            throw new RegraDominioException("O marco deve possuir identificador.");
        }
        if (idadeMeses < 0 || idadeMeses > 72) {
            throw new RegraDominioException("A idade do marco deve estar entre 0 e 72 meses.");
        }
        if (area == null) {
            throw new RegraDominioException("A área do desenvolvimento é obrigatória.");
        }
        if (descricao == null || descricao.isBlank()) {
            throw new RegraDominioException("A descrição do marco é obrigatória.");
        }
        if (fonte == null || fonte.isBlank()) {
            throw new RegraDominioException("A fonte do marco é obrigatória.");
        }

        this.id = id;
        this.idadeMeses = idadeMeses;
        this.area = area;
        this.descricao = descricao.trim();
        this.fonte = fonte.trim();
        this.ativo = ativo;
    }

    public static MarcoDesenvolvimento restaurar(UUID id, int idadeMeses, AreaDesenvolvimento area, String descricao, String fonte, boolean ativo) {
        return new MarcoDesenvolvimento(id, idadeMeses, area, descricao, fonte, ativo);
    }

    public UUID getId() {
        return id;
    }

    public int getIdadeMeses() {
        return idadeMeses;
    }

    public AreaDesenvolvimento getArea() {
        return area;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getFonte() {
        return fonte;
    }

    public boolean isAtivo() {
        return ativo;
    }
}
