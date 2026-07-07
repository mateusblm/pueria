package br.com.pueria.pueria.alimentacao.aplicacao;

import br.com.pueria.pueria.alimentacao.dominio.RegistroAlimentacao;
import br.com.pueria.pueria.alimentacao.dominio.RegistroAlimentacaoRepositorio;
import br.com.pueria.pueria.comum.excecao.RecursoNaoEncontradoException;
import br.com.pueria.pueria.criancas.dominio.Crianca;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AtualizarAlimentacaoUseCase {

    private final AlimentacaoAcesso acesso;
    private final RegistroAlimentacaoRepositorio repositorio;
    private final AnaliseAlimentacaoService analiseService;

    public AtualizarAlimentacaoUseCase(AlimentacaoAcesso acesso, RegistroAlimentacaoRepositorio repositorio, AnaliseAlimentacaoService analiseService) {
        this.acesso = acesso;
        this.repositorio = repositorio;
        this.analiseService = analiseService;
    }

    @Transactional
    public RegistroAlimentacaoDetalhado executar(AtualizarRegistroAlimentacaoComando comando) {
        Crianca crianca = acesso.validar(comando.criancaId(), comando.emailResponsavel());
        RegistroAlimentacao registro = repositorio.buscarPorId(comando.registroId())
                .filter(item -> item.getCriancaId().equals(comando.criancaId()))
                .orElseThrow(() -> new RecursoNaoEncontradoException("Registro alimentar não encontrado."));
        RegistroAlimentacao salvo = repositorio.salvar(registro.atualizar(comando.dados()));
        return new RegistroAlimentacaoDetalhado(salvo, analiseService.analisar(crianca, salvo));
    }
}
