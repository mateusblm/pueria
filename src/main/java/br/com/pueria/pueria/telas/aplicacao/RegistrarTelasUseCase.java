package br.com.pueria.pueria.telas.aplicacao;

import br.com.pueria.pueria.criancas.dominio.Crianca;
import br.com.pueria.pueria.telas.dominio.RegistroTelas;
import br.com.pueria.pueria.telas.dominio.RegistroTelasRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistrarTelasUseCase {

    private final TelasAcesso acesso;
    private final RegistroTelasRepositorio repositorio;
    private final AnaliseTelasService analiseService;

    public RegistrarTelasUseCase(TelasAcesso acesso, RegistroTelasRepositorio repositorio, AnaliseTelasService analiseService) {
        this.acesso = acesso;
        this.repositorio = repositorio;
        this.analiseService = analiseService;
    }

    @Transactional
    public RegistroTelasDetalhado executar(RegistroTelasComando comando) {
        Crianca crianca = acesso.validar(comando.criancaId(), comando.emailResponsavel());
        RegistroTelas salvo = repositorio.salvar(RegistroTelas.registrar(comando.criancaId(), comando.dados()));
        return new RegistroTelasDetalhado(salvo, analiseService.analisar(crianca, salvo));
    }
}
