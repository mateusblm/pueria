package br.com.pueria.pueria.telas.infraestrutura.persistencia;

import br.com.pueria.pueria.telas.dominio.RegistroTelas;
import br.com.pueria.pueria.telas.dominio.RegistroTelasRepositorio;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class RegistroTelasRepositorioJpa implements RegistroTelasRepositorio {

    private final RegistroTelasJpaRepository repository;

    public RegistroTelasRepositorioJpa(RegistroTelasJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public RegistroTelas salvar(RegistroTelas registro) {
        return RegistroTelasMapper.paraDominio(repository.save(RegistroTelasMapper.paraEntidade(registro)));
    }

    @Override
    public Optional<RegistroTelas> buscarPorId(UUID id) {
        return repository.findById(id).map(RegistroTelasMapper::paraDominio);
    }

    @Override
    public List<RegistroTelas> listarPorCrianca(UUID criancaId) {
        return repository.findByCriancaIdOrderByDataRegistroAscCriadoEmAsc(criancaId)
                .stream()
                .map(RegistroTelasMapper::paraDominio)
                .toList();
    }
}
