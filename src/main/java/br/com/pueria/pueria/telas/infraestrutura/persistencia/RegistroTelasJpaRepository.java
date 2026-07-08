package br.com.pueria.pueria.telas.infraestrutura.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

interface RegistroTelasJpaRepository extends JpaRepository<RegistroTelasJpaEntidade, UUID> {
    List<RegistroTelasJpaEntidade> findByCriancaIdOrderByDataRegistroAscCriadoEmAsc(UUID criancaId);
}
