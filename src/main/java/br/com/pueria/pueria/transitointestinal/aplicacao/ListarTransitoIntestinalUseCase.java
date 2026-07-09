package br.com.pueria.pueria.transitointestinal.aplicacao;

import br.com.pueria.pueria.transitointestinal.dominio.RegistroTransitoIntestinalRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class ListarTransitoIntestinalUseCase {

    private final TransitoIntestinalAcesso acesso;
    private final RegistroTransitoIntestinalRepositorio repositorio;
    private final AnaliseTransitoIntestinalService analiseService;

    public ListarTransitoIntestinalUseCase(TransitoIntestinalAcesso acesso, RegistroTransitoIntestinalRepositorio repositorio, AnaliseTransitoIntestinalService analiseService) {
        this.acesso = acesso;
        this.repositorio = repositorio;
        this.analiseService = analiseService;
    }

    @Transactional(readOnly = true)
    public List<RegistroTransitoIntestinalDetalhado> executar(UUID criancaId, String emailResponsavel) {
        acesso.validar(criancaId, emailResponsavel);
        return repositorio.listarPorCrianca(criancaId)
                .stream()
                .sorted(Comparator.comparing(registro -> registro.getDataRegistro(), Comparator.reverseOrder()))
                .map(registro -> new RegistroTransitoIntestinalDetalhado(registro, analiseService.analisar(registro)))
                .toList();
    }
}
