package br.com.pueria.pueria.responsaveis.infraestrutura.persistencia;

import br.com.pueria.pueria.responsaveis.dominio.EstadoConviteCuidador;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ConviteCuidadorJpaRepository extends JpaRepository<ConviteCuidadorJpaEntidade, UUID> {
    boolean existsByCriancaIdAndConvidadoUsuarioIdAndEstado(UUID criancaId, UUID convidadoUsuarioId, EstadoConviteCuidador estado);
    List<ConviteCuidadorJpaEntidade> findByConvidadoUsuarioIdAndEstadoOrderByCriadoEmDesc(UUID usuarioId, EstadoConviteCuidador estado);
    List<ConviteCuidadorJpaEntidade> findByCriancaIdAndEstadoOrderByCriadoEmDesc(UUID criancaId, EstadoConviteCuidador estado);
}
