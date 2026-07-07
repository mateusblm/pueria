package br.com.pueria.pueria.sono.aplicacao;

import br.com.pueria.pueria.comum.excecao.RecursoNaoEncontradoException;
import br.com.pueria.pueria.criancas.dominio.Crianca;
import br.com.pueria.pueria.sono.dominio.RegistroSono;
import br.com.pueria.pueria.sono.dominio.RegistroSonoRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AtualizarSonoUseCase {

    private final SonoAcesso acesso;
    private final RegistroSonoRepositorio repositorio;
    private final AnaliseSonoService analiseService;

    public AtualizarSonoUseCase(SonoAcesso acesso, RegistroSonoRepositorio repositorio, AnaliseSonoService analiseService) {
        this.acesso = acesso;
        this.repositorio = repositorio;
        this.analiseService = analiseService;
    }

    @Transactional
    public RegistroSonoDetalhado executar(AtualizarRegistroSonoComando comando) {
        Crianca crianca = acesso.validar(comando.criancaId(), comando.emailResponsavel());
        RegistroSono registro = repositorio.buscarPorId(comando.registroId())
                .filter(item -> item.getCriancaId().equals(comando.criancaId()))
                .orElseThrow(() -> new RecursoNaoEncontradoException("Registro de sono não encontrado."));
        RegistroSono salvo = repositorio.salvar(registro.atualizar(comando.dados()));
        return new RegistroSonoDetalhado(salvo, analiseService.analisar(crianca, salvo));
    }
}
