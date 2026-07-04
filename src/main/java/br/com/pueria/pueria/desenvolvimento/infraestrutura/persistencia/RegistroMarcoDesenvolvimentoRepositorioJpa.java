package br.com.pueria.pueria.desenvolvimento.infraestrutura.persistencia;

import br.com.pueria.pueria.desenvolvimento.dominio.RegistroMarcoDesenvolvimento;
import br.com.pueria.pueria.desenvolvimento.dominio.RegistroMarcoDesenvolvimentoRepositorio;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class RegistroMarcoDesenvolvimentoRepositorioJpa implements RegistroMarcoDesenvolvimentoRepositorio {

    private final RegistroMarcoDesenvolvimentoJpaRepository repository;

    public RegistroMarcoDesenvolvimentoRepositorioJpa(RegistroMarcoDesenvolvimentoJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public RegistroMarcoDesenvolvimento salvar(RegistroMarcoDesenvolvimento registro) {
        return RegistroMarcoDesenvolvimentoMapper.paraDominio(repository.save(RegistroMarcoDesenvolvimentoMapper.paraEntidade(registro)));
    }

    @Override
    public Optional<RegistroMarcoDesenvolvimento> buscarPorCriancaEMarco(UUID criancaId, UUID marcoId) {
        return repository.findByCriancaIdAndMarcoId(criancaId, marcoId).map(RegistroMarcoDesenvolvimentoMapper::paraDominio);
    }

    @Override
    public List<RegistroMarcoDesenvolvimento> listarPorCrianca(UUID criancaId) {
        return repository.findByCriancaId(criancaId)
                .stream()
                .map(RegistroMarcoDesenvolvimentoMapper::paraDominio)
                .toList();
    }
}
