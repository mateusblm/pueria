package br.com.pueria.pueria.desenvolvimento.dominio;

import br.com.pueria.pueria.comum.excecao.RegraDominioException;

import java.time.LocalDateTime;
import java.util.UUID;

public record RegistroEstimuloDesenvolvimento(UUID id, UUID criancaId, UUID estimuloId, String observacao, LocalDateTime experimentadoEm) {
    public static RegistroEstimuloDesenvolvimento registrar(UUID criancaId, UUID estimuloId, String observacao) {
        String nota = observacao == null || observacao.isBlank() ? null : observacao.trim();
        if (nota != null && nota.length() > 500) {
            throw new RegraDominioException("A observação sobre a atividade deve ter até 500 caracteres.");
        }
        return new RegistroEstimuloDesenvolvimento(UUID.randomUUID(), criancaId, estimuloId, nota, LocalDateTime.now());
    }
}
