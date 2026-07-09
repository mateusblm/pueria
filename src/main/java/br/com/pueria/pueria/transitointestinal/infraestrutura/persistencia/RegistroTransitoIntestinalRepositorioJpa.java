package br.com.pueria.pueria.transitointestinal.infraestrutura.persistencia;

import br.com.pueria.pueria.transitointestinal.dominio.RegistroTransitoIntestinal;
import br.com.pueria.pueria.transitointestinal.dominio.RegistroTransitoIntestinalRepositorio;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
class RegistroTransitoIntestinalRepositorioJpa implements RegistroTransitoIntestinalRepositorio {

    private final RegistroTransitoIntestinalJpaRepository repository;

    RegistroTransitoIntestinalRepositorioJpa(RegistroTransitoIntestinalJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public RegistroTransitoIntestinal salvar(RegistroTransitoIntestinal registro) {
        return RegistroTransitoIntestinalMapper.paraDominio(repository.save(RegistroTransitoIntestinalMapper.paraEntidade(registro)));
    }

    @Override
    public Optional<RegistroTransitoIntestinal> buscarPorId(UUID id) {
        return repository.findById(id).map(RegistroTransitoIntestinalMapper::paraDominio);
    }

    @Override
    public List<RegistroTransitoIntestinal> listarPorCrianca(UUID criancaId) {
        return repository.findByCriancaIdOrderByDataRegistroDescCriadoEmDesc(criancaId)
                .stream()
                .map(RegistroTransitoIntestinalMapper::paraDominio)
                .toList();
    }
}
