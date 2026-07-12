package br.com.pueria.pueria.desenvolvimento.infraestrutura.persistencia;
import br.com.pueria.pueria.desenvolvimento.dominio.*;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository public class RegistroEstimuloDesenvolvimentoRepositorioJpa implements RegistroEstimuloDesenvolvimentoRepositorio {
 private final RegistroEstimuloDesenvolvimentoJpaRepository repository; public RegistroEstimuloDesenvolvimentoRepositorioJpa(RegistroEstimuloDesenvolvimentoJpaRepository repository){this.repository=repository;}
 public RegistroEstimuloDesenvolvimento salvar(RegistroEstimuloDesenvolvimento r){return paraDominio(repository.save(new RegistroEstimuloDesenvolvimentoJpaEntidade(r.id(),r.criancaId(),r.estimuloId(),r.observacao(),r.experimentadoEm())));}
 public List<RegistroEstimuloDesenvolvimento> listarPorCrianca(UUID id){return repository.findByCriancaId(id).stream().map(this::paraDominio).toList();}
 public Optional<RegistroEstimuloDesenvolvimento> buscarPorCriancaEEstimulo(UUID criancaId,UUID estimuloId){return repository.findByCriancaIdAndEstimuloId(criancaId,estimuloId).map(this::paraDominio);}
 private RegistroEstimuloDesenvolvimento paraDominio(RegistroEstimuloDesenvolvimentoJpaEntidade e){return new RegistroEstimuloDesenvolvimento(e.getId(),e.getCriancaId(),e.getEstimuloId(),e.getObservacao(),e.getExperimentadoEm());}
}
