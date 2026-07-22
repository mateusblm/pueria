package br.com.pueria.pueria.responsaveis.dominio;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConviteCuidadorRepositorio {
    ConviteCuidador salvar(ConviteCuidador convite);
    Optional<ConviteCuidador> buscarPorId(UUID id);
    boolean existePendente(UUID criancaId, UUID usuarioId);
    List<ConviteCuidador> listarPendentesPorConvidado(UUID usuarioId);
    List<ConviteCuidador> listarPendentesPorCrianca(UUID criancaId);
}
