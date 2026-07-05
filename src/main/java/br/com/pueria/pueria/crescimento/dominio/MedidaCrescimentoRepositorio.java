package br.com.pueria.pueria.crescimento.dominio;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MedidaCrescimentoRepositorio {
    MedidaCrescimento salvar(MedidaCrescimento medida);
    Optional<MedidaCrescimento> buscarPorId(UUID id);
    List<MedidaCrescimento> listarPorCrianca(UUID criancaId);
    void removerPorId(UUID id);
}
