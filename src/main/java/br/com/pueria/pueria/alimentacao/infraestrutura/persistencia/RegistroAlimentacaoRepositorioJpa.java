package br.com.pueria.pueria.alimentacao.infraestrutura.persistencia;

import br.com.pueria.pueria.alimentacao.dominio.RegistroAlimentacao;
import br.com.pueria.pueria.alimentacao.dominio.RegistroAlimentacaoRepositorio;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class RegistroAlimentacaoRepositorioJpa implements RegistroAlimentacaoRepositorio {

    private final RegistroAlimentacaoJpaRepository repository;

    public RegistroAlimentacaoRepositorioJpa(RegistroAlimentacaoJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public RegistroAlimentacao salvar(RegistroAlimentacao registro) {
        return RegistroAlimentacaoMapper.paraDominio(repository.save(RegistroAlimentacaoMapper.paraEntidade(registro)));
    }

    @Override
    public Optional<RegistroAlimentacao> buscarPorId(UUID id) {
        return repository.findById(id).map(RegistroAlimentacaoMapper::paraDominio);
    }

    @Override
    public List<RegistroAlimentacao> listarPorCrianca(UUID criancaId) {
        return repository.findByCriancaIdOrderByDataRegistroAscCriadoEmAsc(criancaId)
                .stream()
                .map(RegistroAlimentacaoMapper::paraDominio)
                .toList();
    }
}
