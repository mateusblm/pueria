package br.com.pueria.pueria.desenvolvimento.dominio;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RegistroMarcoDesenvolvimentoRepositorio {
    RegistroMarcoDesenvolvimento salvar(RegistroMarcoDesenvolvimento registro);

    Optional<RegistroMarcoDesenvolvimento> buscarPorCriancaEMarco(UUID criancaId, UUID marcoId);

    List<RegistroMarcoDesenvolvimento> listarPorCrianca(UUID criancaId);
}
