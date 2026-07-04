package br.com.pueria.pueria.responsaveis.dominio;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VinculoResponsavelCriancaRepositorio {

    VinculoResponsavelCrianca salvar(VinculoResponsavelCrianca vinculo);

    Optional<VinculoResponsavelCrianca> buscarPorId(UUID id);

    boolean existeResponsavelPrincipal(UUID criancaId);

    boolean usuarioPodeAcessarCrianca(UUID usuarioId, UUID criancaId);

    List<UUID> listarCriancaIdsPorUsuario(UUID usuarioId);

    void removerPorCrianca(UUID criancaId);
}
