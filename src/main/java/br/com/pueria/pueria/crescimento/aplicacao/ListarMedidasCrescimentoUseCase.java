package br.com.pueria.pueria.crescimento.aplicacao;

import br.com.pueria.pueria.crescimento.dominio.MedidaCrescimento;
import br.com.pueria.pueria.crescimento.dominio.MedidaCrescimentoRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ListarMedidasCrescimentoUseCase {

    private final CrescimentoAcesso acesso;
    private final MedidaCrescimentoRepositorio medidaRepositorio;

    public ListarMedidasCrescimentoUseCase(CrescimentoAcesso acesso, MedidaCrescimentoRepositorio medidaRepositorio) {
        this.acesso = acesso;
        this.medidaRepositorio = medidaRepositorio;
    }

    @Transactional(readOnly = true)
    public List<MedidaCrescimento> executar(UUID criancaId, String emailResponsavel) {
        acesso.validar(criancaId, emailResponsavel);
        return medidaRepositorio.listarPorCrianca(criancaId);
    }
}
