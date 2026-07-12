package br.com.pueria.pueria.desenvolvimento.dominio;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RelatoDesenvolvimentoRepositorio {
    RelatoDesenvolvimento salvar(RelatoDesenvolvimento relato);
    List<RelatoDesenvolvimento> listarPorCrianca(UUID criancaId);
    Optional<RelatoDesenvolvimento> buscarPorId(UUID id);
    void remover(RelatoDesenvolvimento relato);
}
