package br.com.pueria.pueria.consentimentos.infraestrutura.persistencia;

import br.com.pueria.pueria.consentimentos.dominio.Consentimento;
import br.com.pueria.pueria.consentimentos.dominio.ConsentimentoRepositorio;
import br.com.pueria.pueria.consentimentos.dominio.TipoConsentimento;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class ConsentimentoRepositorioJpa implements ConsentimentoRepositorio {

    private final ConsentimentoJpaRepository repository;

    public ConsentimentoRepositorioJpa(ConsentimentoJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Consentimento salvar(Consentimento consentimento) {
        ConsentimentoJpaEntidade entidade = ConsentimentoMapper.paraEntidade(consentimento);
        ConsentimentoJpaEntidade salva = repository.save(entidade);
        return ConsentimentoMapper.paraDominio(salva);
    }

    @Override
    public Optional<Consentimento> buscarPorId(UUID id) {
        return repository.findById(id).map(ConsentimentoMapper::paraDominio);
    }

    @Override
    public boolean existeAceite(UUID usuarioId, UUID criancaId, TipoConsentimento tipo) {
        return repository.existsByUsuarioIdAndCriancaIdAndTipoAndAceitoTrue(usuarioId, criancaId, tipo);
    }

    @Override
    public void removerPorCrianca(UUID criancaId) {
        repository.deleteByCriancaId(criancaId);
    }
}
