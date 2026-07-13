package br.com.pueria.pueria.desenvolvimento.infraestrutura.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EstimuloDesenvolvimentoJpaRepository extends JpaRepository<EstimuloDesenvolvimentoJpaEntidade, UUID> {
    List<EstimuloDesenvolvimentoJpaEntidade> findByAtivoTrueAndIdadeInicialMesesLessThanEqualAndIdadeFinalMesesGreaterThanEqualOrderByAreaAsc(int inicio, int fim);

    @Query(value = """
            SELECT estimulo.*
            FROM estimulos_desenvolvimento estimulo
            JOIN marcos_estimulos_desenvolvimento vinculo ON vinculo.estimulo_id = estimulo.id
            WHERE vinculo.marco_id = :marcoId AND estimulo.ativo = TRUE
            """, nativeQuery = true)
    Optional<EstimuloDesenvolvimentoJpaEntidade> findAtivoByMarcoId(UUID marcoId);
}
