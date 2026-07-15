package br.com.pueria.pueria.saude.dominio;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RegistroSaudeRepositorio {
    RegistroSaude salvar(RegistroSaude registro);
    Optional<RegistroSaude> buscarPorId(UUID id);
    List<RegistroSaude> listarPorCrianca(UUID criancaId);
    void removerPorId(UUID id);
}
