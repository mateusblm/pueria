package br.com.pueria.pueria.desenvolvimento.dominio;

import br.com.pueria.pueria.comum.excecao.RegraDominioException;

import java.time.LocalDateTime;
import java.util.UUID;

public class RegistroMarcoDesenvolvimento {

    private static final int TAMANHO_MAXIMO_OBSERVACAO = 500;

    private final UUID id;
    private final UUID criancaId;
    private final UUID marcoId;
    private final StatusMarcoDesenvolvimento status;
    private final String observacao;
    private final LocalDateTime registradoEm;
    private final LocalDateTime atualizadoEm;

    private RegistroMarcoDesenvolvimento(
            UUID id,
            UUID criancaId,
            UUID marcoId,
            StatusMarcoDesenvolvimento status,
            String observacao,
            LocalDateTime registradoEm,
            LocalDateTime atualizadoEm
    ) {
        this.id = validarId(id, "O registro deve possuir identificador.");
        this.criancaId = validarId(criancaId, "A criança do registro é obrigatória.");
        this.marcoId = validarId(marcoId, "O marco do registro é obrigatório.");
        if (status == null) {
            throw new RegraDominioException("O status do marco é obrigatório.");
        }
        this.status = status;
        this.observacao = validarObservacao(observacao);
        if (registradoEm == null) {
            throw new RegraDominioException("A data do registro é obrigatória.");
        }
        this.registradoEm = registradoEm;
        this.atualizadoEm = atualizadoEm;
    }

    public static RegistroMarcoDesenvolvimento registrar(UUID criancaId, UUID marcoId, StatusMarcoDesenvolvimento status, String observacao) {
        return new RegistroMarcoDesenvolvimento(UUID.randomUUID(), criancaId, marcoId, status, observacao, LocalDateTime.now(), null);
    }

    public static RegistroMarcoDesenvolvimento restaurar(
            UUID id,
            UUID criancaId,
            UUID marcoId,
            StatusMarcoDesenvolvimento status,
            String observacao,
            LocalDateTime registradoEm,
            LocalDateTime atualizadoEm
    ) {
        return new RegistroMarcoDesenvolvimento(id, criancaId, marcoId, status, observacao, registradoEm, atualizadoEm);
    }

    public RegistroMarcoDesenvolvimento atualizar(StatusMarcoDesenvolvimento status, String observacao) {
        return new RegistroMarcoDesenvolvimento(id, criancaId, marcoId, status, observacao, registradoEm, LocalDateTime.now());
    }

    private static UUID validarId(UUID id, String mensagem) {
        if (id == null) {
            throw new RegraDominioException(mensagem);
        }
        return id;
    }

    private static String validarObservacao(String observacao) {
        if (observacao == null || observacao.isBlank()) {
            return null;
        }
        String tratada = observacao.trim();
        if (tratada.length() > TAMANHO_MAXIMO_OBSERVACAO) {
            throw new RegraDominioException("A observação deve ter no máximo 500 caracteres.");
        }
        return tratada;
    }

    public UUID getId() { return id; }
    public UUID getCriancaId() { return criancaId; }
    public UUID getMarcoId() { return marcoId; }
    public StatusMarcoDesenvolvimento getStatus() { return status; }
    public String getObservacao() { return observacao; }
    public LocalDateTime getRegistradoEm() { return registradoEm; }
    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
}
