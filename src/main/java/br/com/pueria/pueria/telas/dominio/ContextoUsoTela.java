package br.com.pueria.pueria.telas.dominio;

import java.util.Objects;

public record ContextoUsoTela(TipoDispositivoTela dispositivo, TipoConteudoTela conteudo) {
    public ContextoUsoTela {
        Objects.requireNonNull(dispositivo, "O dispositivo e obrigatorio.");
        conteudo = conteudo == null ? TipoConteudoTela.NAO_INFORMADO : conteudo;
    }
}
