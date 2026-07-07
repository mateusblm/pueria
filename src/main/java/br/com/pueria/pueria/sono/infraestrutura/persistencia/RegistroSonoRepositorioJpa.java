package br.com.pueria.pueria.sono.infraestrutura.persistencia;

import br.com.pueria.pueria.sono.dominio.RegistroSono;
import br.com.pueria.pueria.sono.dominio.RegistroSonoRepositorio;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class RegistroSonoRepositorioJpa implements RegistroSonoRepositorio {

    private final RegistroSonoJpaRepository repository;

    public RegistroSonoRepositorioJpa(RegistroSonoJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public RegistroSono salvar(RegistroSono registro) {
        return RegistroSonoMapper.paraDominio(repository.save(RegistroSonoMapper.paraEntidade(registro)));
    }

    @Override
    public Optional<RegistroSono> buscarPorId(UUID id) {
        return repository.findById(id).map(RegistroSonoMapper::paraDominio);
    }

    @Override
    public List<RegistroSono> listarPorCrianca(UUID criancaId) {
        return repository.findByCriancaIdOrderByDataRegistroAscCriadoEmAsc(criancaId)
                .stream()
                .map(RegistroSonoMapper::paraDominio)
                .toList();
    }
}
