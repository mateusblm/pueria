package br.com.pueria.pueria.alimentacao.dominio;

import br.com.pueria.pueria.comum.excecao.RegraDominioException;

public record AlimentoRegistroAlimentacao(
        String codigo,
        String nome,
        GrupoAlimento grupo
) {
    public AlimentoRegistroAlimentacao {
        if (codigo == null || codigo.isBlank()) {
            throw new RegraDominioException("O código do alimento é obrigatório.");
        }
        if (nome == null || nome.isBlank()) {
            throw new RegraDominioException("O nome do alimento é obrigatório.");
        }
        if (grupo == null) {
            throw new RegraDominioException("O grupo do alimento é obrigatório.");
        }

        codigo = codigo.trim().toLowerCase();
        nome = nome.trim().replaceAll("\\s+", " ");

        if (codigo.length() > 80) {
            throw new RegraDominioException("O código do alimento deve ter no máximo 80 caracteres.");
        }
        if (nome.length() > 120) {
            throw new RegraDominioException("O nome do alimento deve ter no máximo 120 caracteres.");
        }
    }
}
