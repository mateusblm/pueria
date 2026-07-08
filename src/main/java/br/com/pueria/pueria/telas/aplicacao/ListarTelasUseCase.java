package br.com.pueria.pueria.telas.aplicacao;

import br.com.pueria.pueria.criancas.dominio.Crianca;
import br.com.pueria.pueria.telas.dominio.RegistroTelasRepositorio;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class ListarTelasUseCase {

    private final TelasAcesso acesso;
    private final RegistroTelasRepositorio repositorio;
    private final AnaliseTelasService analiseService;

    public ListarTelasUseCase(TelasAcesso acesso, RegistroTelasRepositorio repositorio, AnaliseTelasService analiseService) {
        this.acesso = acesso;
        this.repositorio = repositorio;
        this.analiseService = analiseService;
    }

    public List<RegistroTelasDetalhado> executar(UUID criancaId, String emailResponsavel) {
        Crianca crianca = acesso.validar(criancaId, emailResponsavel);
        return repositorio.listarPorCrianca(criancaId)
                .stream()
                .sorted(Comparator.comparing(registro -> registro.getDataRegistro()))
                .map(registro -> new RegistroTelasDetalhado(registro, analiseService.analisar(crianca, registro)))
                .toList();
    }
}
