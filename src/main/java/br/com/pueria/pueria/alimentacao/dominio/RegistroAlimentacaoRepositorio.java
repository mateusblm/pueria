package br.com.pueria.pueria.alimentacao.dominio;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RegistroAlimentacaoRepositorio {
    RegistroAlimentacao salvar(RegistroAlimentacao registro);
    Optional<RegistroAlimentacao> buscarPorId(UUID id);
    List<RegistroAlimentacao> listarPorCrianca(UUID criancaId);
}
