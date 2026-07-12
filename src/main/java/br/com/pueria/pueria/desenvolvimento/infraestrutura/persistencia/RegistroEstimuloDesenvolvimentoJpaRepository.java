package br.com.pueria.pueria.desenvolvimento.infraestrutura.persistencia;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface RegistroEstimuloDesenvolvimentoJpaRepository extends JpaRepository<RegistroEstimuloDesenvolvimentoJpaEntidade, UUID>{ List<RegistroEstimuloDesenvolvimentoJpaEntidade> findByCriancaIdOrderByExperimentadoEmDesc(UUID criancaId); Optional<RegistroEstimuloDesenvolvimentoJpaEntidade> findByCriancaIdAndEstimuloId(UUID criancaId,UUID estimuloId); }
