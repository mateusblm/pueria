package br.com.pueria.pueria.desenvolvimento.infraestrutura.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MarcoDesenvolvimentoJpaRepository extends JpaRepository<MarcoDesenvolvimentoJpaEntidade, UUID> {
    List<MarcoDesenvolvimentoJpaEntidade> findByAtivoTrueAndIdadeMesesLessThanEqualOrderByIdadeMesesAscAreaAscDescricaoAsc(int idadeMeses);
}
