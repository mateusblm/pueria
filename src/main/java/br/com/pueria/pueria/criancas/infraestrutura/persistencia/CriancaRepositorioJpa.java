package br.com.pueria.pueria.criancas.infraestrutura.persistencia;

import br.com.pueria.pueria.criancas.dominio.Crianca;
import br.com.pueria.pueria.criancas.dominio.CriancaRepositorio;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class CriancaRepositorioJpa implements CriancaRepositorio {

    private final CriancaJpaRepository criancaJpaRepository;

    public CriancaRepositorioJpa(CriancaJpaRepository criancaJpaRepository) {
        this.criancaJpaRepository = criancaJpaRepository;
    }

    @Override
    public Crianca salvar(Crianca crianca) {
        CriancaJpaEntidade entidade = CriancaMapper.paraEntidade(crianca);
        CriancaJpaEntidade salva = criancaJpaRepository.save(entidade);
        return CriancaMapper.paraDominio(salva);
    }

    @Override
    public Optional<Crianca> buscarPorId(UUID id) {
        return criancaJpaRepository.findById(id).map(CriancaMapper::paraDominio);
    }

    @Override
    public List<Crianca> listarPorIds(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }

        return criancaJpaRepository.findAllByIdInOrderByNomeAsc(ids)
                .stream()
                .map(CriancaMapper::paraDominio)
                .toList();
    }

    @Override
    public void removerPorId(UUID id) {
        criancaJpaRepository.deleteById(id);
    }
}
