package br.com.pueria.pueria.desenvolvimento.dominio;

import java.util.UUID;

public record EstimuloDesenvolvimento(
        UUID id, int idadeInicialMeses, int idadeFinalMeses, AreaDesenvolvimento area,
        String titulo, String descricao, String cuidado, String fonte, String versaoCatalogo, boolean ativo
) { }
