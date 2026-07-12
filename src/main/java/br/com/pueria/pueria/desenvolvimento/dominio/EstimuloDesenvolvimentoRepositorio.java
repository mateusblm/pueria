package br.com.pueria.pueria.desenvolvimento.dominio;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EstimuloDesenvolvimentoRepositorio {
    List<EstimuloDesenvolvimento> listarAtivosParaIdade(int idadeMeses);
    Optional<EstimuloDesenvolvimento> buscarPorId(UUID id);
}
