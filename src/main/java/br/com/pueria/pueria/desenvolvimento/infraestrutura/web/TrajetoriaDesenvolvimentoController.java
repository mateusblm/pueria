package br.com.pueria.pueria.desenvolvimento.infraestrutura.web;

import br.com.pueria.pueria.desenvolvimento.aplicacao.ListarTrajetoriaDesenvolvimentoUseCase;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/criancas/{criancaId}/desenvolvimento/trajetoria")
public class TrajetoriaDesenvolvimentoController {

    private final ListarTrajetoriaDesenvolvimentoUseCase listarUseCase;

    public TrajetoriaDesenvolvimentoController(ListarTrajetoriaDesenvolvimentoUseCase listarUseCase) {
        this.listarUseCase = listarUseCase;
    }

    @GetMapping
    public List<EventoTrajetoriaDesenvolvimentoResponse> listar(@PathVariable UUID criancaId, Authentication authentication) {
        return listarUseCase.executar(criancaId, authentication.getName()).stream()
                .map(EventoTrajetoriaDesenvolvimentoResponse::de)
                .toList();
    }
}
