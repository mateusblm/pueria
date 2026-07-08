package br.com.pueria.pueria.telas.aplicacao;

import br.com.pueria.pueria.comum.excecao.RecursoNaoEncontradoException;
import br.com.pueria.pueria.criancas.dominio.Crianca;
import br.com.pueria.pueria.telas.dominio.RegistroTelas;
import br.com.pueria.pueria.telas.dominio.RegistroTelasRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AtualizarTelasUseCase {

    private final TelasAcesso acesso;
    private final RegistroTelasRepositorio repositorio;
    private final AnaliseTelasService analiseService;

    public AtualizarTelasUseCase(TelasAcesso acesso, RegistroTelasRepositorio repositorio, AnaliseTelasService analiseService) {
        this.acesso = acesso;
        this.repositorio = repositorio;
        this.analiseService = analiseService;
    }

    @Transactional
    public RegistroTelasDetalhado executar(AtualizarRegistroTelasComando comando) {
        Crianca crianca = acesso.validar(comando.criancaId(), comando.emailResponsavel());
        RegistroTelas registro = repositorio.buscarPorId(comando.registroId())
                .filter(item -> item.getCriancaId().equals(comando.criancaId()))
                .orElseThrow(() -> new RecursoNaoEncontradoException("Registro de telas nao encontrado."));
        RegistroTelas salvo = repositorio.salvar(registro.atualizar(comando.dados()));
        return new RegistroTelasDetalhado(salvo, analiseService.analisar(crianca, salvo));
    }
}
