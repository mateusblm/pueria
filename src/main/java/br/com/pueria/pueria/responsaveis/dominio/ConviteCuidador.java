package br.com.pueria.pueria.responsaveis.dominio;

import br.com.pueria.pueria.comum.excecao.RegraDominioException;
import java.time.LocalDateTime;
import java.util.UUID;

public class ConviteCuidador {
    private final UUID id; private final UUID criancaId; private final UUID convidadoUsuarioId; private final UUID criadoPorUsuarioId; private final Parentesco parentesco; private final EstadoConviteCuidador estado; private final LocalDateTime criadoEm; private final LocalDateTime respondidoEm;
    private ConviteCuidador(UUID id, UUID criancaId, UUID convidadoUsuarioId, UUID criadoPorUsuarioId, Parentesco parentesco, EstadoConviteCuidador estado, LocalDateTime criadoEm, LocalDateTime respondidoEm) { this.id = obrigatorio(id, "O convite deve possuir identificador."); this.criancaId = obrigatorio(criancaId, "A criança é obrigatória."); this.convidadoUsuarioId = obrigatorio(convidadoUsuarioId, "O cuidador é obrigatório."); this.criadoPorUsuarioId = obrigatorio(criadoPorUsuarioId, "Quem convidou é obrigatório."); this.parentesco = parentesco == null ? Parentesco.OUTRO : parentesco; this.estado = estado == null ? EstadoConviteCuidador.PENDENTE : estado; this.criadoEm = criadoEm == null ? LocalDateTime.now() : criadoEm; this.respondidoEm = respondidoEm; }
    public static ConviteCuidador criar(UUID criancaId, UUID convidadoUsuarioId, UUID criadoPorUsuarioId, Parentesco parentesco) { return new ConviteCuidador(UUID.randomUUID(), criancaId, convidadoUsuarioId, criadoPorUsuarioId, parentesco, EstadoConviteCuidador.PENDENTE, LocalDateTime.now(), null); }
    public static ConviteCuidador restaurar(UUID id, UUID criancaId, UUID convidadoUsuarioId, UUID criadoPorUsuarioId, Parentesco parentesco, EstadoConviteCuidador estado, LocalDateTime criadoEm, LocalDateTime respondidoEm) { return new ConviteCuidador(id, criancaId, convidadoUsuarioId, criadoPorUsuarioId, parentesco, estado, criadoEm, respondidoEm); }
    public ConviteCuidador aceitar() { validarPendente(); return new ConviteCuidador(id, criancaId, convidadoUsuarioId, criadoPorUsuarioId, parentesco, EstadoConviteCuidador.ACEITO, criadoEm, LocalDateTime.now()); }
    public ConviteCuidador recusar() { validarPendente(); return new ConviteCuidador(id, criancaId, convidadoUsuarioId, criadoPorUsuarioId, parentesco, EstadoConviteCuidador.RECUSADO, criadoEm, LocalDateTime.now()); }
    private void validarPendente() { if (estado != EstadoConviteCuidador.PENDENTE) throw new RegraDominioException("Este convite já foi respondido."); }
    private static UUID obrigatorio(UUID valor, String mensagem) { if (valor == null) throw new RegraDominioException(mensagem); return valor; }
    public UUID getId() { return id; } public UUID getCriancaId() { return criancaId; } public UUID getConvidadoUsuarioId() { return convidadoUsuarioId; } public UUID getCriadoPorUsuarioId() { return criadoPorUsuarioId; } public Parentesco getParentesco() { return parentesco; } public EstadoConviteCuidador getEstado() { return estado; } public LocalDateTime getCriadoEm() { return criadoEm; } public LocalDateTime getRespondidoEm() { return respondidoEm; }
}
