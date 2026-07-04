package br.com.pueria.pueria.criancas.infraestrutura.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

interface CriancaJpaRepository extends JpaRepository<CriancaJpaEntidade, UUID> {

    List<CriancaJpaEntidade> findAllByIdInOrderByNomeAsc(Collection<UUID> ids);
}
