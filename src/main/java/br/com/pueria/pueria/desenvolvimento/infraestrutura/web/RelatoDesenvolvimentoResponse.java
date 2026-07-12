package br.com.pueria.pueria.desenvolvimento.infraestrutura.web;

import br.com.pueria.pueria.desenvolvimento.dominio.RelatoDesenvolvimento;
import br.com.pueria.pueria.desenvolvimento.dominio.TipoRelatoDesenvolvimento;

import java.time.LocalDateTime;
import java.util.UUID;

public record RelatoDesenvolvimentoResponse(UUID id, TipoRelatoDesenvolvimento tipo, String descricao, LocalDateTime registradoEm) {
    public static RelatoDesenvolvimentoResponse de(RelatoDesenvolvimento relato) {
        return new RelatoDesenvolvimentoResponse(relato.getId(), relato.getTipo(), relato.getDescricao(), relato.getRegistradoEm());
    }
}
