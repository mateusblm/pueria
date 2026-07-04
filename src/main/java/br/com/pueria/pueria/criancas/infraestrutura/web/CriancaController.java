package br.com.pueria.pueria.criancas.infraestrutura.web;

import br.com.pueria.pueria.criancas.aplicacao.BuscarCriancaUseCase;
import br.com.pueria.pueria.criancas.aplicacao.CriarCriancaUseCase;
import br.com.pueria.pueria.criancas.dominio.Crianca;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/criancas")
public class CriancaController {

    private final CriarCriancaUseCase criarCriancaUseCase;
    private final BuscarCriancaUseCase buscarCriancaUseCase;

    public CriancaController(CriarCriancaUseCase criarCriancaUseCase, BuscarCriancaUseCase buscarCriancaUseCase) {
        this.criarCriancaUseCase = criarCriancaUseCase;
        this.buscarCriancaUseCase = buscarCriancaUseCase;
    }

    @PostMapping
    public ResponseEntity<CriancaResponse> criar(
            @Valid @RequestBody CriarCriancaRequest request,
            Authentication authentication
    ) {
        Crianca crianca = criarCriancaUseCase.executar(request.paraComando(authentication.getName()));
        return ResponseEntity
                .created(URI.create("/api/criancas/" + crianca.getId()))
                .body(CriancaResponse.de(crianca));
    }

    @GetMapping("/{id}")
    public CriancaResponse buscarPorId(@PathVariable UUID id, Authentication authentication) {
        Crianca crianca = buscarCriancaUseCase.executar(id, authentication.getName());
        return CriancaResponse.de(crianca);
    }
}
