package br.com.pueria.pueria.responsaveis.dominio;

import br.com.pueria.pueria.comum.excecao.RegraDominioException;

import java.time.LocalDateTime;
import java.util.UUID;

public class VinculoResponsavelCrianca {

    private final UUID id;
    private final UUID usuarioId;
    private final UUID criancaId;
    private final Parentesco parentesco;
    private final boolean principal;
    private final LocalDateTime criadoEm;

    private VinculoResponsavelCrianca(
            UUID id,
            UUID usuarioId,
            UUID criancaId,
            Parentesco parentesco,
            boolean principal,
            LocalDateTime criadoEm
    ) {
        this.id = validarId(id, "O vínculo deve possuir identificador.");
        this.usuarioId = validarId(usuarioId, "O responsável é obrigatório.");
        this.criancaId = validarId(criancaId, "A criança é obrigatória.");
        this.parentesco = validarParentesco(parentesco);
        this.principal = principal;
        this.criadoEm = validarCriadoEm(criadoEm);
    }

    public static VinculoResponsavelCrianca criarPrincipal(UUID usuarioId, UUID criancaId, Parentesco parentesco) {
        return new VinculoResponsavelCrianca(
                UUID.randomUUID(),
                usuarioId,
                criancaId,
                parentesco,
                true,
                LocalDateTime.now()
        );
    }

    public static VinculoResponsavelCrianca restaurar(
            UUID id,
            UUID usuarioId,
            UUID criancaId,
            Parentesco parentesco,
            boolean principal,
            LocalDateTime criadoEm
    ) {
        return new VinculoResponsavelCrianca(id, usuarioId, criancaId, parentesco, principal, criadoEm);
    }

    private static UUID validarId(UUID id, String mensagem) {
        if (id == null) {
            throw new RegraDominioException(mensagem);
        }
        return id;
    }

    private static Parentesco validarParentesco(Parentesco parentesco) {
        if (parentesco == null) {
            throw new RegraDominioException("O parentesco do responsável é obrigatório.");
        }
        return parentesco;
    }

    private static LocalDateTime validarCriadoEm(LocalDateTime criadoEm) {
        if (criadoEm == null) {
            throw new RegraDominioException("A data de criação do vínculo é obrigatória.");
        }
        return criadoEm;
    }

    public UUID getId() {
        return id;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public UUID getCriancaId() {
        return criancaId;
    }

    public Parentesco getParentesco() {
        return parentesco;
    }

    public boolean isPrincipal() {
        return principal;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }
}
