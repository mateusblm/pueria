package br.com.pueria.pueria.desenvolvimento.infraestrutura.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RegistroMarcoDesenvolvimentoJpaRepository extends JpaRepository<RegistroMarcoDesenvolvimentoJpaEntidade, UUID> {
    Optional<RegistroMarcoDesenvolvimentoJpaEntidade> findByCriancaIdAndMarcoId(UUID criancaId, UUID marcoId);

    List<RegistroMarcoDesenvolvimentoJpaEntidade> findByCriancaId(UUID criancaId);
}
