package br.com.pueria.pueria.desenvolvimento.infraestrutura.persistencia;

import br.com.pueria.pueria.desenvolvimento.dominio.RelatoDesenvolvimento;
import br.com.pueria.pueria.desenvolvimento.dominio.RelatoDesenvolvimentoRepositorio;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class RelatoDesenvolvimentoRepositorioJpa implements RelatoDesenvolvimentoRepositorio {
    private final RelatoDesenvolvimentoJpaRepository repository;
    public RelatoDesenvolvimentoRepositorioJpa(RelatoDesenvolvimentoJpaRepository repository) { this.repository = repository; }
    public RelatoDesenvolvimento salvar(RelatoDesenvolvimento relato) { return paraDominio(repository.save(paraEntidade(relato))); }
    public List<RelatoDesenvolvimento> listarPorCrianca(UUID criancaId) { return repository.findByCriancaIdOrderByRegistradoEmDesc(criancaId).stream().map(this::paraDominio).toList(); }
    public Optional<RelatoDesenvolvimento> buscarPorId(UUID id) { return repository.findById(id).map(this::paraDominio); }
    public void remover(RelatoDesenvolvimento relato) { repository.deleteById(relato.getId()); }
    private RelatoDesenvolvimento paraDominio(RelatoDesenvolvimentoJpaEntidade entidade) { return RelatoDesenvolvimento.restaurar(entidade.getId(), entidade.getCriancaId(), entidade.getTipo(), entidade.getDescricao(), entidade.getRegistradoEm()); }
    private RelatoDesenvolvimentoJpaEntidade paraEntidade(RelatoDesenvolvimento relato) { return new RelatoDesenvolvimentoJpaEntidade(relato.getId(), relato.getCriancaId(), relato.getTipo(), relato.getDescricao(), relato.getRegistradoEm()); }
}
