package br.com.pueria.pueria.sono.infraestrutura.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

interface RegistroSonoJpaRepository extends JpaRepository<RegistroSonoJpaEntidade, UUID> {
    List<RegistroSonoJpaEntidade> findByCriancaIdOrderByDataRegistroAscCriadoEmAsc(UUID criancaId);
}
