package br.com.pueria.pueria.responsaveis.infraestrutura.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface VinculoResponsavelCriancaJpaRepository extends JpaRepository<VinculoResponsavelCriancaJpaEntidade, UUID> {

    boolean existsByCriancaIdAndPrincipalTrue(UUID criancaId);

    boolean existsByUsuarioIdAndCriancaId(UUID usuarioId, UUID criancaId);

    boolean existsByUsuarioIdAndCriancaIdAndPrincipalTrue(UUID usuarioId, UUID criancaId);

    List<VinculoResponsavelCriancaJpaEntidade> findByCriancaIdOrderByPrincipalDescCriadoEmAsc(UUID criancaId);

    @Query("select v.criancaId from VinculoResponsavelCriancaJpaEntidade v where v.usuarioId = :usuarioId")
    List<UUID> findCriancaIdsByUsuarioId(@Param("usuarioId") UUID usuarioId);

    void deleteByCriancaId(UUID criancaId);
}
