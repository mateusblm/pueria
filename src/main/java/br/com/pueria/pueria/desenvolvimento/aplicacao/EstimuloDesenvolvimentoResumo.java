package br.com.pueria.pueria.desenvolvimento.aplicacao;

import br.com.pueria.pueria.desenvolvimento.dominio.AreaDesenvolvimento;

import java.time.LocalDateTime;
import java.util.UUID;

public record EstimuloDesenvolvimentoResumo(UUID id, AreaDesenvolvimento area, String titulo, String descricao,
                                             String cuidado, String fonte, String versaoCatalogo,
                                             boolean experimentado, String observacao, LocalDateTime experimentadoEm) { }
