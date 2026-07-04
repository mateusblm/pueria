package br.com.pueria.pueria.desenvolvimento.infraestrutura.web;

import br.com.pueria.pueria.desenvolvimento.aplicacao.ListarMarcosDesenvolvimentoUseCase;
import br.com.pueria.pueria.desenvolvimento.aplicacao.RegistrarMarcoDesenvolvimentoUseCase;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/criancas/{criancaId}/desenvolvimento/marcos")
public class DesenvolvimentoController {

    private final ListarMarcosDesenvolvimentoUseCase listarUseCase;
    private final RegistrarMarcoDesenvolvimentoUseCase registrarUseCase;

    public DesenvolvimentoController(ListarMarcosDesenvolvimentoUseCase listarUseCase, RegistrarMarcoDesenvolvimentoUseCase registrarUseCase) {
        this.listarUseCase = listarUseCase;
        this.registrarUseCase = registrarUseCase;
    }

    @GetMapping
    public List<MarcoDesenvolvimentoResponse> listar(@PathVariable UUID criancaId, Authentication authentication) {
        return listarUseCase.executar(criancaId, authentication.getName())
                .stream()
                .map(MarcoDesenvolvimentoResponse::de)
                .toList();
    }

    @PutMapping("/{marcoId}")
    public void registrar(
            @PathVariable UUID criancaId,
            @PathVariable UUID marcoId,
            @Valid @RequestBody RegistrarMarcoDesenvolvimentoRequest request,
            Authentication authentication
    ) {
        registrarUseCase.executar(request.paraComando(criancaId, marcoId, authentication.getName()));
    }
}
