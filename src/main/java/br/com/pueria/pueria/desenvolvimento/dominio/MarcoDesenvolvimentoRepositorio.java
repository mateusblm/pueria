package br.com.pueria.pueria.desenvolvimento.dominio;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MarcoDesenvolvimentoRepositorio {
    Optional<MarcoDesenvolvimento> buscarPorId(UUID id);

    List<MarcoDesenvolvimento> listarAtivosAteIdadeMeses(int idadeMeses);
}
