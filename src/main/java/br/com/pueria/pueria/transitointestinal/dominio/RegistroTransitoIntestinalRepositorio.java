package br.com.pueria.pueria.transitointestinal.dominio;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RegistroTransitoIntestinalRepositorio {
    RegistroTransitoIntestinal salvar(RegistroTransitoIntestinal registro);
    Optional<RegistroTransitoIntestinal> buscarPorId(UUID id);
    List<RegistroTransitoIntestinal> listarPorCrianca(UUID criancaId);
}
