package br.com.pueria.pueria.consentimentos.infraestrutura.persistencia;

import br.com.pueria.pueria.consentimentos.dominio.TipoConsentimento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ConsentimentoJpaRepository extends JpaRepository<ConsentimentoJpaEntidade, UUID> {

    boolean existsByUsuarioIdAndCriancaIdAndTipoAndAceitoTrue(UUID usuarioId, UUID criancaId, TipoConsentimento tipo);
}
