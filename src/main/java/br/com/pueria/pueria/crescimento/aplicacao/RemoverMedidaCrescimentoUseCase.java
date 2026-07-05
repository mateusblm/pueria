package br.com.pueria.pueria.crescimento.aplicacao;

import br.com.pueria.pueria.comum.excecao.RecursoNaoEncontradoException;
import br.com.pueria.pueria.crescimento.dominio.MedidaCrescimento;
import br.com.pueria.pueria.crescimento.dominio.MedidaCrescimentoRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class RemoverMedidaCrescimentoUseCase {

    private final CrescimentoAcesso acesso;
    private final MedidaCrescimentoRepositorio medidaRepositorio;

    public RemoverMedidaCrescimentoUseCase(CrescimentoAcesso acesso, MedidaCrescimentoRepositorio medidaRepositorio) {
        this.acesso = acesso;
        this.medidaRepositorio = medidaRepositorio;
    }

    @Transactional
    public void executar(UUID criancaId, UUID medidaId, String emailResponsavel) {
        acesso.validar(criancaId, emailResponsavel);
        MedidaCrescimento medida = medidaRepositorio.buscarPorId(medidaId)
                .filter(item -> item.getCriancaId().equals(criancaId))
                .orElseThrow(() -> new RecursoNaoEncontradoException("Medida de crescimento não encontrada."));
        medidaRepositorio.removerPorId(medida.getId());
    }
}
