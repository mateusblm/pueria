package br.com.pueria.pueria.alimentacao.aplicacao;

import br.com.pueria.pueria.alimentacao.dominio.RegistroAlimentacaoRepositorio;
import br.com.pueria.pueria.criancas.dominio.Crianca;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class ListarAlimentacaoUseCase {

    private final AlimentacaoAcesso acesso;
    private final RegistroAlimentacaoRepositorio repositorio;
    private final AnaliseAlimentacaoService analiseService;

    public ListarAlimentacaoUseCase(AlimentacaoAcesso acesso, RegistroAlimentacaoRepositorio repositorio, AnaliseAlimentacaoService analiseService) {
        this.acesso = acesso;
        this.repositorio = repositorio;
        this.analiseService = analiseService;
    }

    public List<RegistroAlimentacaoDetalhado> executar(UUID criancaId, String emailResponsavel) {
        Crianca crianca = acesso.validar(criancaId, emailResponsavel);
        return repositorio.listarPorCrianca(criancaId)
                .stream()
                .sorted(Comparator.comparing(registro -> registro.getDataRegistro()))
                .map(registro -> new RegistroAlimentacaoDetalhado(registro, analiseService.analisar(crianca, registro)))
                .toList();
    }
}
