package br.com.pueria.pueria.desenvolvimento.infraestrutura.persistencia;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface EstimuloDesenvolvimentoJpaRepository extends JpaRepository<EstimuloDesenvolvimentoJpaEntidade, UUID> { List<EstimuloDesenvolvimentoJpaEntidade> findByAtivoTrueAndIdadeInicialMesesLessThanEqualAndIdadeFinalMesesGreaterThanEqualOrderByAreaAsc(int inicio, int fim); }
