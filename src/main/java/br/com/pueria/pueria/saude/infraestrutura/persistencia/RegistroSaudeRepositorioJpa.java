package br.com.pueria.pueria.saude.infraestrutura.persistencia;

import br.com.pueria.pueria.saude.dominio.DadosRegistroSaude;
import br.com.pueria.pueria.saude.dominio.RegistroSaude;
import br.com.pueria.pueria.saude.dominio.RegistroSaudeRepositorio;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class RegistroSaudeRepositorioJpa implements RegistroSaudeRepositorio {
    private final RegistroSaudeJpaRepository repository;

    public RegistroSaudeRepositorioJpa(RegistroSaudeJpaRepository repository) { this.repository = repository; }

    @Override
    public RegistroSaude salvar(RegistroSaude registro) { return paraDominio(repository.save(paraEntidade(registro))); }

    @Override
    public Optional<RegistroSaude> buscarPorId(UUID id) { return repository.findById(id).map(this::paraDominio); }

    @Override
    public List<RegistroSaude> listarPorCrianca(UUID criancaId) { return repository.findByCriancaIdOrderByDataRegistroDescCriadoEmDesc(criancaId).stream().map(this::paraDominio).toList(); }

    @Override
    public void removerPorId(UUID id) { repository.deleteById(id); }

    private RegistroSaudeJpaEntidade paraEntidade(RegistroSaude r) {
        return new RegistroSaudeJpaEntidade(r.getId(), r.getCriancaId(), r.getTipo(), r.getDataRegistro(), r.getDescricao(), r.getCriadoEm(), r.getAtualizadoEm());
    }

    private RegistroSaude paraDominio(RegistroSaudeJpaEntidade e) {
        return RegistroSaude.restaurar(e.getId(), e.getCriancaId(), new DadosRegistroSaude(e.getTipo(), e.getDataRegistro(), e.getDescricao()), e.getCriadoEm(), e.getAtualizadoEm());
    }
}
