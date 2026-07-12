package br.com.pueria.pueria.desenvolvimento.dominio;

import br.com.pueria.pueria.comum.excecao.RegraDominioException;

import java.time.LocalDateTime;
import java.util.UUID;

public class RelatoDesenvolvimento {

    private final UUID id;
    private final UUID criancaId;
    private final TipoRelatoDesenvolvimento tipo;
    private final String descricao;
    private final LocalDateTime registradoEm;

    private RelatoDesenvolvimento(UUID id, UUID criancaId, TipoRelatoDesenvolvimento tipo, String descricao, LocalDateTime registradoEm) {
        if (id == null || criancaId == null || tipo == null || registradoEm == null) {
            throw new RegraDominioException("Os dados do relato de desenvolvimento são obrigatórios.");
        }
        if (descricao == null || descricao.isBlank() || descricao.trim().length() > 500) {
            throw new RegraDominioException("Descreva o que foi percebido em até 500 caracteres.");
        }
        this.id = id;
        this.criancaId = criancaId;
        this.tipo = tipo;
        this.descricao = descricao.trim();
        this.registradoEm = registradoEm;
    }

    public static RelatoDesenvolvimento registrar(UUID criancaId, TipoRelatoDesenvolvimento tipo, String descricao) {
        return new RelatoDesenvolvimento(UUID.randomUUID(), criancaId, tipo, descricao, LocalDateTime.now());
    }

    public static RelatoDesenvolvimento restaurar(UUID id, UUID criancaId, TipoRelatoDesenvolvimento tipo, String descricao, LocalDateTime registradoEm) {
        return new RelatoDesenvolvimento(id, criancaId, tipo, descricao, registradoEm);
    }

    public UUID getId() { return id; }
    public UUID getCriancaId() { return criancaId; }
    public TipoRelatoDesenvolvimento getTipo() { return tipo; }
    public String getDescricao() { return descricao; }
    public LocalDateTime getRegistradoEm() { return registradoEm; }
}
