package br.com.pueria.pueria.responsaveis.infraestrutura.persistencia;

import br.com.pueria.pueria.responsaveis.dominio.VinculoResponsavelCrianca;
import br.com.pueria.pueria.responsaveis.dominio.VinculoResponsavelCriancaRepositorio;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class VinculoResponsavelCriancaRepositorioJpa implements VinculoResponsavelCriancaRepositorio {

    private final VinculoResponsavelCriancaJpaRepository repository;

    public VinculoResponsavelCriancaRepositorioJpa(VinculoResponsavelCriancaJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public VinculoResponsavelCrianca salvar(VinculoResponsavelCrianca vinculo) {
        VinculoResponsavelCriancaJpaEntidade entidade = VinculoResponsavelCriancaMapper.paraEntidade(vinculo);
        VinculoResponsavelCriancaJpaEntidade salva = repository.save(entidade);
        return VinculoResponsavelCriancaMapper.paraDominio(salva);
    }

    @Override
    public Optional<VinculoResponsavelCrianca> buscarPorId(UUID id) {
        return repository.findById(id).map(VinculoResponsavelCriancaMapper::paraDominio);
    }

    @Override
    public boolean existeResponsavelPrincipal(UUID criancaId) {
        return repository.existsByCriancaIdAndPrincipalTrue(criancaId);
    }

    @Override
    public boolean usuarioPodeAcessarCrianca(UUID usuarioId, UUID criancaId) {
        return repository.existsByUsuarioIdAndCriancaId(usuarioId, criancaId);
    }

    @Override
    public boolean usuarioEhResponsavelPrincipal(UUID usuarioId, UUID criancaId) {
        return repository.existsByUsuarioIdAndCriancaIdAndPrincipalTrue(usuarioId, criancaId);
    }

    @Override
    public List<VinculoResponsavelCrianca> listarPorCrianca(UUID criancaId) {
        return repository.findByCriancaIdOrderByPrincipalDescCriadoEmAsc(criancaId).stream()
                .map(VinculoResponsavelCriancaMapper::paraDominio)
                .toList();
    }

    @Override
    public List<UUID> listarCriancaIdsPorUsuario(UUID usuarioId) {
        return repository.findCriancaIdsByUsuarioId(usuarioId);
    }

    @Override
    public void removerPorId(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public void removerPorCrianca(UUID criancaId) {
        repository.deleteByCriancaId(criancaId);
    }
}
