package br.com.pueria.pueria.alimentacao.aplicacao;

import br.com.pueria.pueria.alimentacao.dominio.RegistroAlimentacao;
import br.com.pueria.pueria.alimentacao.dominio.RegistroAlimentacaoRepositorio;
import br.com.pueria.pueria.criancas.dominio.Crianca;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistrarAlimentacaoUseCase {

    private final AlimentacaoAcesso acesso;
    private final RegistroAlimentacaoRepositorio repositorio;
    private final AnaliseAlimentacaoService analiseService;

    public RegistrarAlimentacaoUseCase(AlimentacaoAcesso acesso, RegistroAlimentacaoRepositorio repositorio, AnaliseAlimentacaoService analiseService) {
        this.acesso = acesso;
        this.repositorio = repositorio;
        this.analiseService = analiseService;
    }

    @Transactional
    public RegistroAlimentacaoDetalhado executar(RegistroAlimentacaoComando comando) {
        Crianca crianca = acesso.validar(comando.criancaId(), comando.emailResponsavel());
        RegistroAlimentacao registro = RegistroAlimentacao.registrar(comando.criancaId(), comando.dados());
        RegistroAlimentacao salvo = repositorio.salvar(registro);
        return new RegistroAlimentacaoDetalhado(salvo, analiseService.analisar(crianca, salvo));
    }
}
