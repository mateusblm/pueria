package br.com.pueria.pueria.saude.infraestrutura.web;

import br.com.pueria.pueria.saude.dominio.RegistroSaude;
import br.com.pueria.pueria.saude.dominio.TipoRegistroSaude;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record RegistroSaudeResponse(UUID id, TipoRegistroSaude tipo, LocalDate dataRegistro, String descricao, LocalDateTime criadoEm, LocalDateTime atualizadoEm) {
    static RegistroSaudeResponse de(RegistroSaude registro) {
        return new RegistroSaudeResponse(registro.getId(), registro.getTipo(), registro.getDataRegistro(), registro.getDescricao(), registro.getCriadoEm(), registro.getAtualizadoEm());
    }
}
