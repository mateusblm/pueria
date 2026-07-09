package br.com.pueria.pueria.transitointestinal.infraestrutura.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

interface RegistroTransitoIntestinalJpaRepository extends JpaRepository<RegistroTransitoIntestinalJpaEntidade, UUID> {
    List<RegistroTransitoIntestinalJpaEntidade> findByCriancaIdOrderByDataRegistroDescCriadoEmDesc(UUID criancaId);
}
