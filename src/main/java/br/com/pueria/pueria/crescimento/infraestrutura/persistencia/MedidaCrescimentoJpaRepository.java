package br.com.pueria.pueria.crescimento.infraestrutura.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

interface MedidaCrescimentoJpaRepository extends JpaRepository<MedidaCrescimentoJpaEntidade, UUID> {
    List<MedidaCrescimentoJpaEntidade> findByCriancaIdOrderByDataMedicaoAscCriadoEmAsc(UUID criancaId);
}
