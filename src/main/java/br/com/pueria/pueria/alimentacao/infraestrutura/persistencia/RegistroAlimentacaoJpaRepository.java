package br.com.pueria.pueria.alimentacao.infraestrutura.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

interface RegistroAlimentacaoJpaRepository extends JpaRepository<RegistroAlimentacaoJpaEntidade, UUID> {
    List<RegistroAlimentacaoJpaEntidade> findByCriancaIdOrderByDataRegistroAscCriadoEmAsc(UUID criancaId);
}
