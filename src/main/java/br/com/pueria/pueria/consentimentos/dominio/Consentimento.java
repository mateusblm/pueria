package br.com.pueria.pueria.consentimentos.dominio;

import br.com.pueria.pueria.comum.excecao.RegraDominioException;

import java.time.LocalDateTime;
import java.util.UUID;

public class Consentimento {

    private final UUID id;
    private final UUID usuarioId;
    private final UUID criancaId;
    private final TipoConsentimento tipo;
    private final String versaoTermo;
    private final boolean aceito;
    private final LocalDateTime dataAceite;

    private Consentimento(
            UUID id,
            UUID usuarioId,
            UUID criancaId,
            TipoConsentimento tipo,
            String versaoTermo,
            boolean aceito,
            LocalDateTime dataAceite
    ) {
        this.id = validarId(id, "O consentimento deve possuir identificador.");
        this.usuarioId = validarId(usuarioId, "O usuário do consentimento é obrigatório.");
        this.criancaId = validarId(criancaId, "A criança do consentimento é obrigatória.");
        this.tipo = validarTipo(tipo);
        this.versaoTermo = validarVersaoTermo(versaoTermo);
        this.aceito = validarAceite(aceito);
        this.dataAceite = validarDataAceite(dataAceite);
    }

    public static Consentimento registrarAceite(
            UUID usuarioId,
            UUID criancaId,
            TipoConsentimento tipo,
            String versaoTermo,
            boolean aceito
    ) {
        return new Consentimento(
                UUID.randomUUID(),
                usuarioId,
                criancaId,
                tipo,
                versaoTermo,
                aceito,
                LocalDateTime.now()
        );
    }

    public static Consentimento restaurar(
            UUID id,
            UUID usuarioId,
            UUID criancaId,
            TipoConsentimento tipo,
            String versaoTermo,
            boolean aceito,
            LocalDateTime dataAceite
    ) {
        return new Consentimento(id, usuarioId, criancaId, tipo, versaoTermo, aceito, dataAceite);
    }

    private static UUID validarId(UUID id, String mensagem) {
        if (id == null) {
            throw new RegraDominioException(mensagem);
        }
        return id;
    }

    private static TipoConsentimento validarTipo(TipoConsentimento tipo) {
        if (tipo == null) {
            throw new RegraDominioException("O tipo de consentimento é obrigatório.");
        }
        return tipo;
    }

    private static String validarVersaoTermo(String versaoTermo) {
        if (versaoTermo == null || versaoTermo.isBlank()) {
            throw new RegraDominioException("A versão do termo de consentimento é obrigatória.");
        }
        String versaoTratada = versaoTermo.trim();
        if (versaoTratada.length() > 30) {
            throw new RegraDominioException("A versão do termo de consentimento deve possuir no máximo 30 caracteres.");
        }
        return versaoTratada;
    }

    private static boolean validarAceite(boolean aceito) {
        if (!aceito) {
            throw new RegraDominioException("O consentimento deve ser aceito para cadastrar a criança.");
        }
        return true;
    }

    private static LocalDateTime validarDataAceite(LocalDateTime dataAceite) {
        if (dataAceite == null) {
            throw new RegraDominioException("A data de aceite do consentimento é obrigatória.");
        }
        return dataAceite;
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

    public TipoConsentimento getTipo() {
        return tipo;
    }

    public String getVersaoTermo() {
        return versaoTermo;
    }

    public boolean isAceito() {
        return aceito;
    }

    public LocalDateTime getDataAceite() {
        return dataAceite;
    }
}
