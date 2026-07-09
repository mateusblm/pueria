package br.com.pueria.pueria.transitointestinal.aplicacao;

import br.com.pueria.pueria.transitointestinal.dominio.RegistroTransitoIntestinal;
import br.com.pueria.pueria.transitointestinal.dominio.RegistroTransitoIntestinalRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistrarTransitoIntestinalUseCase {

    private final TransitoIntestinalAcesso acesso;
    private final RegistroTransitoIntestinalRepositorio repositorio;
    private final AnaliseTransitoIntestinalService analiseService;

    public RegistrarTransitoIntestinalUseCase(TransitoIntestinalAcesso acesso, RegistroTransitoIntestinalRepositorio repositorio, AnaliseTransitoIntestinalService analiseService) {
        this.acesso = acesso;
        this.repositorio = repositorio;
        this.analiseService = analiseService;
    }

    @Transactional
    public RegistroTransitoIntestinalDetalhado executar(RegistroTransitoIntestinalComando comando) {
        acesso.validar(comando.criancaId(), comando.emailResponsavel());
        RegistroTransitoIntestinal salvo = repositorio.salvar(RegistroTransitoIntestinal.registrar(comando.criancaId(), comando.dados()));
        return new RegistroTransitoIntestinalDetalhado(salvo, analiseService.analisar(salvo));
    }
}
