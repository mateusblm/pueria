package br.com.pueria.pueria.transitointestinal.aplicacao;

import br.com.pueria.pueria.comum.excecao.RecursoNaoEncontradoException;
import br.com.pueria.pueria.transitointestinal.dominio.RegistroTransitoIntestinal;
import br.com.pueria.pueria.transitointestinal.dominio.RegistroTransitoIntestinalRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AtualizarTransitoIntestinalUseCase {

    private final TransitoIntestinalAcesso acesso;
    private final RegistroTransitoIntestinalRepositorio repositorio;
    private final AnaliseTransitoIntestinalService analiseService;

    public AtualizarTransitoIntestinalUseCase(TransitoIntestinalAcesso acesso, RegistroTransitoIntestinalRepositorio repositorio, AnaliseTransitoIntestinalService analiseService) {
        this.acesso = acesso;
        this.repositorio = repositorio;
        this.analiseService = analiseService;
    }

    @Transactional
    public RegistroTransitoIntestinalDetalhado executar(AtualizarRegistroTransitoIntestinalComando comando) {
        acesso.validar(comando.criancaId(), comando.emailResponsavel());
        RegistroTransitoIntestinal registro = repositorio.buscarPorId(comando.registroId())
                .filter(item -> item.getCriancaId().equals(comando.criancaId()))
                .orElseThrow(() -> new RecursoNaoEncontradoException("Registro intestinal nao encontrado."));
        RegistroTransitoIntestinal atualizado = repositorio.salvar(registro.atualizar(comando.dados()));
        return new RegistroTransitoIntestinalDetalhado(atualizado, analiseService.analisar(atualizado));
    }
}
