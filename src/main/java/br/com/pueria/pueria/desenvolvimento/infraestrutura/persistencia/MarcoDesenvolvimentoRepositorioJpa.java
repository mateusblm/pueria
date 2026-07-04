package br.com.pueria.pueria.desenvolvimento.infraestrutura.persistencia;

import br.com.pueria.pueria.desenvolvimento.dominio.MarcoDesenvolvimento;
import br.com.pueria.pueria.desenvolvimento.dominio.MarcoDesenvolvimentoRepositorio;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class MarcoDesenvolvimentoRepositorioJpa implements MarcoDesenvolvimentoRepositorio {

    private final MarcoDesenvolvimentoJpaRepository repository;

    public MarcoDesenvolvimentoRepositorioJpa(MarcoDesenvolvimentoJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<MarcoDesenvolvimento> buscarPorId(UUID id) {
        return repository.findById(id).map(MarcoDesenvolvimentoMapper::paraDominio);
    }

    @Override
    public List<MarcoDesenvolvimento> listarAtivosAteIdadeMeses(int idadeMeses) {
        return repository.findByAtivoTrueAndIdadeMesesLessThanEqualOrderByIdadeMesesAscAreaAscDescricaoAsc(idadeMeses)
                .stream()
                .map(MarcoDesenvolvimentoMapper::paraDominio)
                .toList();
    }
}
