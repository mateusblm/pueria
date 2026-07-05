package br.com.pueria.pueria.crescimento.aplicacao;

import br.com.pueria.pueria.crescimento.dominio.MedidaCrescimento;
import br.com.pueria.pueria.crescimento.dominio.MedidaCrescimentoRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistrarMedidaCrescimentoUseCase {

    private final CrescimentoAcesso acesso;
    private final MedidaCrescimentoRepositorio medidaRepositorio;

    public RegistrarMedidaCrescimentoUseCase(CrescimentoAcesso acesso, MedidaCrescimentoRepositorio medidaRepositorio) {
        this.acesso = acesso;
        this.medidaRepositorio = medidaRepositorio;
    }

    @Transactional
    public MedidaCrescimento executar(RegistrarMedidaCrescimentoComando comando) {
        acesso.validar(comando.criancaId(), comando.emailResponsavel());
        MedidaCrescimento medida = MedidaCrescimento.registrar(
                comando.criancaId(),
                comando.dataMedicao(),
                comando.pesoKg(),
                comando.comprimentoCm(),
                comando.perimetroCefalicoCm(),
                comando.origem(),
                comando.observacao()
        );
        return medidaRepositorio.salvar(medida);
    }
}
