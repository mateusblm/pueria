package br.com.pueria.pueria.saude.infraestrutura.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RegistroSaudeJpaRepository extends JpaRepository<RegistroSaudeJpaEntidade, UUID> {
    List<RegistroSaudeJpaEntidade> findByCriancaIdOrderByDataRegistroDescCriadoEmDesc(UUID criancaId);
}
