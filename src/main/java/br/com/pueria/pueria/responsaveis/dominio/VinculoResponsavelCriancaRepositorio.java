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

    default void removerPorId(UUID id) {
        throw new UnsupportedOperationException("Remoção individual de vínculo não suportada.");
    }

    void removerPorCrianca(UUID criancaId);
}
