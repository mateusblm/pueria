package br.com.pueria.pueria.desenvolvimento.infraestrutura.web;
import br.com.pueria.pueria.desenvolvimento.aplicacao.EstimuloDesenvolvimentoResumo;
import br.com.pueria.pueria.desenvolvimento.dominio.AreaDesenvolvimento;
import java.time.LocalDateTime;
import java.util.UUID;
public record EstimuloDesenvolvimentoResponse(UUID id, AreaDesenvolvimento area, String titulo, String descricao, String cuidado, String fonte, String versaoCatalogo, boolean experimentado, String observacao, LocalDateTime experimentadoEm) { public static EstimuloDesenvolvimentoResponse de(EstimuloDesenvolvimentoResumo item){return new EstimuloDesenvolvimentoResponse(item.id(),item.area(),item.titulo(),item.descricao(),item.cuidado(),item.fonte(),item.versaoCatalogo(),item.experimentado(),item.observacao(),item.experimentadoEm());} }
