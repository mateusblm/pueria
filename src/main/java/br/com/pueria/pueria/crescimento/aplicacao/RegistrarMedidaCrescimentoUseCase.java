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
        var crianca = acesso.validar(comando.criancaId(), comando.emailResponsavel());
        if (comando.dataMedicao().isBefore(crianca.getDataNascimento())) {
            throw new br.com.pueria.pueria.comum.excecao.RegraDominioException("A data da medição não pode ser anterior ao nascimento.");
        }
        MedidaCrescimento medida = MedidaCrescimento.registrar(
                comando.criancaId(),
                comando.dataMedicao(),
                comando.pesoKg(),
                comando.comprimentoCm(),
                comando.perimetroCefalicoCm(),
                comando.origem(),
                comando.responsavelMedicao(),
                comando.observacao()
        );
        return medidaRepositorio.salvar(medida);
    }
}
