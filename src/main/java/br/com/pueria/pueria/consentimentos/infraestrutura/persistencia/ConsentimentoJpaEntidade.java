package br.com.pueria.pueria.consentimentos.infraestrutura.persistencia;

import br.com.pueria.pueria.consentimentos.dominio.TipoConsentimento;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "consentimentos")
public class ConsentimentoJpaEntidade {

    @Id
    private UUID id;

    @Column(name = "usuario_id", nullable = false)
    private UUID usuarioId;

    @Column(name = "crianca_id", nullable = false)
    private UUID criancaId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 80)
    private TipoConsentimento tipo;

    @Column(name = "versao_termo", nullable = false, length = 30)
    private String versaoTermo;

    @Column(nullable = false)
    private boolean aceito;

    @Column(name = "data_aceite", nullable = false)
    private LocalDateTime dataAceite;

    protected ConsentimentoJpaEntidade() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(UUID usuarioId) {
        this.usuarioId = usuarioId;
    }

    public UUID getCriancaId() {
        return criancaId;
    }

    public void setCriancaId(UUID criancaId) {
        this.criancaId = criancaId;
    }

    public TipoConsentimento getTipo() {
        return tipo;
    }

    public void setTipo(TipoConsentimento tipo) {
        this.tipo = tipo;
    }

    public String getVersaoTermo() {
        return versaoTermo;
    }

    public void setVersaoTermo(String versaoTermo) {
        this.versaoTermo = versaoTermo;
    }

    public boolean isAceito() {
        return aceito;
    }

    public void setAceito(boolean aceito) {
        this.aceito = aceito;
    }

    public LocalDateTime getDataAceite() {
        return dataAceite;
    }

    public void setDataAceite(LocalDateTime dataAceite) {
        this.dataAceite = dataAceite;
    }
}
