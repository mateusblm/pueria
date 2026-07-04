package br.com.pueria.pueria.responsaveis.infraestrutura.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VinculoResponsavelCriancaJpaRepository extends JpaRepository<VinculoResponsavelCriancaJpaEntidade, UUID> {

    boolean existsByCriancaIdAndPrincipalTrue(UUID criancaId);

    boolean existsByUsuarioIdAndCriancaId(UUID usuarioId, UUID criancaId);
}
