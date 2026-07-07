package br.com.pueria.pueria.sono.dominio;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RegistroSonoRepositorio {
    RegistroSono salvar(RegistroSono registro);
    Optional<RegistroSono> buscarPorId(UUID id);
    List<RegistroSono> listarPorCrianca(UUID criancaId);
}
