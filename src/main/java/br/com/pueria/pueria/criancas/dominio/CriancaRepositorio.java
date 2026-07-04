package br.com.pueria.pueria.criancas.dominio;

import java.util.Optional;
import java.util.UUID;

public interface CriancaRepositorio {

    Crianca salvar(Crianca crianca);

    Optional<Crianca> buscarPorId(UUID id);
}
