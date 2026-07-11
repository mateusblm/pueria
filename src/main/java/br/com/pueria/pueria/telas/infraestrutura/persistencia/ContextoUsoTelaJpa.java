package br.com.pueria.pueria.telas.infraestrutura.persistencia;

import br.com.pueria.pueria.telas.dominio.TipoConteudoTela;
import br.com.pueria.pueria.telas.dominio.TipoDispositivoTela;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Embeddable
class ContextoUsoTelaJpa {
    @Enumerated(EnumType.STRING)
    private TipoDispositivoTela dispositivo;

    @Enumerated(EnumType.STRING)
    private TipoConteudoTela conteudo;

    protected ContextoUsoTelaJpa() {}

    ContextoUsoTelaJpa(TipoDispositivoTela dispositivo, TipoConteudoTela conteudo) {
        this.dispositivo = dispositivo;
        this.conteudo = conteudo;
    }

    TipoDispositivoTela getDispositivo() { return dispositivo; }
    TipoConteudoTela getConteudo() { return conteudo; }
}
