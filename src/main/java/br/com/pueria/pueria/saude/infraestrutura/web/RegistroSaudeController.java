package br.com.pueria.pueria.saude.infraestrutura.web;

import br.com.pueria.pueria.saude.aplicacao.GerenciarRegistrosSaudeUseCase;
import br.com.pueria.pueria.saude.dominio.RegistroSaude;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/criancas/{criancaId}/saude/registros")
public class RegistroSaudeController {
    private final GerenciarRegistrosSaudeUseCase useCase;

    public RegistroSaudeController(GerenciarRegistrosSaudeUseCase useCase) { this.useCase = useCase; }

    @GetMapping
    public List<RegistroSaudeResponse> listar(@PathVariable UUID criancaId, Authentication authentication) {
        return useCase.listar(criancaId, authentication.getName()).stream().map(RegistroSaudeResponse::de).toList();
    }

    @PostMapping
    public ResponseEntity<RegistroSaudeResponse> registrar(@PathVariable UUID criancaId, @Valid @RequestBody RegistroSaudeRequest request, Authentication authentication) {
        RegistroSaude registro = useCase.registrar(criancaId, request.paraDominio(), authentication.getName());
        return ResponseEntity.created(URI.create("/api/criancas/" + criancaId + "/saude/registros/" + registro.getId())).body(RegistroSaudeResponse.de(registro));
    }

    @PutMapping("/{registroId}")
    public RegistroSaudeResponse atualizar(@PathVariable UUID criancaId, @PathVariable UUID registroId, @Valid @RequestBody RegistroSaudeRequest request, Authentication authentication) {
        return RegistroSaudeResponse.de(useCase.atualizar(criancaId, registroId, request.paraDominio(), authentication.getName()));
    }

    @DeleteMapping("/{registroId}")
    public ResponseEntity<Void> remover(@PathVariable UUID criancaId, @PathVariable UUID registroId, Authentication authentication) {
        useCase.remover(criancaId, registroId, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
