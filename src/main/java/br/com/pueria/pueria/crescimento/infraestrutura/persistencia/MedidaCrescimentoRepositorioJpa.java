package br.com.pueria.pueria.crescimento.infraestrutura.persistencia;

import br.com.pueria.pueria.crescimento.dominio.MedidaCrescimento;
import br.com.pueria.pueria.crescimento.dominio.MedidaCrescimentoRepositorio;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class MedidaCrescimentoRepositorioJpa implements MedidaCrescimentoRepositorio {

    private final MedidaCrescimentoJpaRepository repository;

    public MedidaCrescimentoRepositorioJpa(MedidaCrescimentoJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public MedidaCrescimento salvar(MedidaCrescimento medida) {
        return MedidaCrescimentoMapper.paraDominio(repository.save(MedidaCrescimentoMapper.paraEntidade(medida)));
    }

    @Override
    public Optional<MedidaCrescimento> buscarPorId(UUID id) {
        return repository.findById(id).map(MedidaCrescimentoMapper::paraDominio);
    }

    @Override
    public List<MedidaCrescimento> listarPorCrianca(UUID criancaId) {
        return repository.findByCriancaIdOrderByDataMedicaoAscCriadoEmAsc(criancaId)
                .stream()
                .map(MedidaCrescimentoMapper::paraDominio)
                .toList();
    }

    @Override
    public void removerPorId(UUID id) {
        repository.deleteById(id);
    }
}
