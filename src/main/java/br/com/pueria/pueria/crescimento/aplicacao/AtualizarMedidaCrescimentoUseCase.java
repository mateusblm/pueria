package br.com.pueria.pueria.crescimento.aplicacao;

import br.com.pueria.pueria.comum.excecao.RecursoNaoEncontradoException;
import br.com.pueria.pueria.crescimento.dominio.MedidaCrescimento;
import br.com.pueria.pueria.crescimento.dominio.MedidaCrescimentoRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AtualizarMedidaCrescimentoUseCase {

    private final CrescimentoAcesso acesso;
    private final MedidaCrescimentoRepositorio medidaRepositorio;

    public AtualizarMedidaCrescimentoUseCase(CrescimentoAcesso acesso, MedidaCrescimentoRepositorio medidaRepositorio) {
        this.acesso = acesso;
        this.medidaRepositorio = medidaRepositorio;
    }

    @Transactional
    public MedidaCrescimento executar(AtualizarMedidaCrescimentoComando comando) {
        acesso.validar(comando.criancaId(), comando.emailResponsavel());
        MedidaCrescimento medida = medidaRepositorio.buscarPorId(comando.medidaId())
                .filter(item -> item.getCriancaId().equals(comando.criancaId()))
                .orElseThrow(() -> new RecursoNaoEncontradoException("Medida de crescimento não encontrada."));

        return medidaRepositorio.salvar(medida.atualizar(
                comando.dataMedicao(),
                comando.pesoKg(),
                comando.comprimentoCm(),
                comando.perimetroCefalicoCm(),
                comando.origem(),
                comando.observacao()
        ));
    }
}
