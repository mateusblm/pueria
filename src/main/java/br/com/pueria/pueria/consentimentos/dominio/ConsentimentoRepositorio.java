package br.com.pueria.pueria.consentimentos.dominio;

import java.util.Optional;
import java.util.UUID;

public interface ConsentimentoRepositorio {

    Consentimento salvar(Consentimento consentimento);

    Optional<Consentimento> buscarPorId(UUID id);

    boolean existeAceite(UUID usuarioId, UUID criancaId, TipoConsentimento tipo);
}
