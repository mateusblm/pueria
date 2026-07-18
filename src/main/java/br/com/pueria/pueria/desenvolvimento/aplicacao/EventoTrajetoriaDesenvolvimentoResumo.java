package br.com.pueria.pueria.desenvolvimento.aplicacao;

import br.com.pueria.pueria.desenvolvimento.dominio.AreaDesenvolvimento;

import java.time.LocalDateTime;

public record EventoTrajetoriaDesenvolvimentoResumo(
        String tipo,
        String descricaoMarco,
        AreaDesenvolvimento area,
        LocalDateTime registradoEm
) {
}
