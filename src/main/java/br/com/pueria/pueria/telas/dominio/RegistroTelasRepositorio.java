package br.com.pueria.pueria.telas.dominio;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RegistroTelasRepositorio {
    RegistroTelas salvar(RegistroTelas registro);
    Optional<RegistroTelas> buscarPorId(UUID id);
    List<RegistroTelas> listarPorCrianca(UUID criancaId);
}
