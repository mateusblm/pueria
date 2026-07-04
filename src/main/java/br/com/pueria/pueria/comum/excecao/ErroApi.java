package br.com.pueria.pueria.comum.excecao;

import java.time.LocalDateTime;
import java.util.List;

public record ErroApi(
        LocalDateTime dataHora,
        int status,
        String erro,
        List<String> mensagens
) {

    public static ErroApi criar(int status, String erro, List<String> mensagens) {
        return new ErroApi(LocalDateTime.now(), status, erro, mensagens);
    }
}
