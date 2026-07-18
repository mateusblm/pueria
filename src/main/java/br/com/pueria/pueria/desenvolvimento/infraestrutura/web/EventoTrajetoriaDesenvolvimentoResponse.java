package br.com.pueria.pueria.desenvolvimento.infraestrutura.web;

import br.com.pueria.pueria.desenvolvimento.aplicacao.EventoTrajetoriaDesenvolvimentoResumo;
import br.com.pueria.pueria.desenvolvimento.dominio.AreaDesenvolvimento;

import java.time.LocalDateTime;

public record EventoTrajetoriaDesenvolvimentoResponse(
        String tipo,
        String descricaoMarco,
        AreaDesenvolvimento area,
        LocalDateTime registradoEm
) {
    public static EventoTrajetoriaDesenvolvimentoResponse de(EventoTrajetoriaDesenvolvimentoResumo resumo) {
        return new EventoTrajetoriaDesenvolvimentoResponse(
                resumo.tipo(),
                resumo.descricaoMarco(),
                resumo.area(),
                resumo.registradoEm()
        );
    }
}
