package br.com.pueria.pueria.criancas.dominio;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CriancaRepositorio {

    Crianca salvar(Crianca crianca);

    Optional<Crianca> buscarPorId(UUID id);

    List<Crianca> listarPorIds(List<UUID> ids);

    void removerPorId(UUID id);
}
