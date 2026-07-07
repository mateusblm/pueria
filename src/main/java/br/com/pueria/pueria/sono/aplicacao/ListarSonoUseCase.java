package br.com.pueria.pueria.sono.aplicacao;

import br.com.pueria.pueria.criancas.dominio.Crianca;
import br.com.pueria.pueria.sono.dominio.RegistroSonoRepositorio;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class ListarSonoUseCase {

    private final SonoAcesso acesso;
    private final RegistroSonoRepositorio repositorio;
    private final AnaliseSonoService analiseService;

    public ListarSonoUseCase(SonoAcesso acesso, RegistroSonoRepositorio repositorio, AnaliseSonoService analiseService) {
        this.acesso = acesso;
        this.repositorio = repositorio;
        this.analiseService = analiseService;
    }

    public List<RegistroSonoDetalhado> executar(UUID criancaId, String emailResponsavel) {
        Crianca crianca = acesso.validar(criancaId, emailResponsavel);
        return repositorio.listarPorCrianca(criancaId)
                .stream()
                .sorted(Comparator.comparing(registro -> registro.getDataRegistro()))
                .map(registro -> new RegistroSonoDetalhado(registro, analiseService.analisar(crianca, registro)))
                .toList();
    }
}
