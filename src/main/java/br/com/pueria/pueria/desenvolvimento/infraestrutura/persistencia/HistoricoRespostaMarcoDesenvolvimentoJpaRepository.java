package br.com.pueria.pueria.desenvolvimento.infraestrutura.persistencia;
import org.springframework.data.jpa.repository.JpaRepository; import java.util.UUID;
public interface HistoricoRespostaMarcoDesenvolvimentoJpaRepository extends JpaRepository<HistoricoRespostaMarcoDesenvolvimentoJpaEntidade,UUID>{}
