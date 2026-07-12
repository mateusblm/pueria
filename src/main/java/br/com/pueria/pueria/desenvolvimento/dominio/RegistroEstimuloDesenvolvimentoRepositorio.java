package br.com.pueria.pueria.desenvolvimento.dominio;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RegistroEstimuloDesenvolvimentoRepositorio {
    RegistroEstimuloDesenvolvimento salvar(RegistroEstimuloDesenvolvimento registro);
    List<RegistroEstimuloDesenvolvimento> listarPorCrianca(UUID criancaId);
    Optional<RegistroEstimuloDesenvolvimento> buscarPorCriancaEEstimulo(UUID criancaId, UUID estimuloId);
}
