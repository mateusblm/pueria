package br.com.pueria.pueria.sono.aplicacao;

import br.com.pueria.pueria.criancas.dominio.Crianca;
import br.com.pueria.pueria.sono.dominio.RegistroSono;
import br.com.pueria.pueria.sono.dominio.RegistroSonoRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistrarSonoUseCase {

    private final SonoAcesso acesso;
    private final RegistroSonoRepositorio repositorio;
    private final AnaliseSonoService analiseService;

    public RegistrarSonoUseCase(SonoAcesso acesso, RegistroSonoRepositorio repositorio, AnaliseSonoService analiseService) {
        this.acesso = acesso;
        this.repositorio = repositorio;
        this.analiseService = analiseService;
    }

    @Transactional
    public RegistroSonoDetalhado executar(RegistroSonoComando comando) {
        Crianca crianca = acesso.validar(comando.criancaId(), comando.emailResponsavel());
        RegistroSono registro = RegistroSono.registrar(comando.criancaId(), comando.dados());
        RegistroSono salvo = repositorio.salvar(registro);
        return new RegistroSonoDetalhado(salvo, analiseService.analisar(crianca, salvo));
    }
}
