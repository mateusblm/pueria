package br.com.pueria.pueria.desenvolvimento.infraestrutura.web;

import br.com.pueria.pueria.desenvolvimento.aplicacao.GerenciarRelatosDesenvolvimentoUseCase;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/criancas/{criancaId}/desenvolvimento/relatos")
public class RelatoDesenvolvimentoController {
    private final GerenciarRelatosDesenvolvimentoUseCase useCase;
    public RelatoDesenvolvimentoController(GerenciarRelatosDesenvolvimentoUseCase useCase) { this.useCase = useCase; }
    @GetMapping public List<RelatoDesenvolvimentoResponse> listar(@PathVariable UUID criancaId, Authentication authentication) { return useCase.listar(criancaId, authentication.getName()).stream().map(RelatoDesenvolvimentoResponse::de).toList(); }
    @PostMapping public RelatoDesenvolvimentoResponse registrar(@PathVariable UUID criancaId, @Valid @RequestBody RegistrarRelatoDesenvolvimentoRequest request, Authentication authentication) { return RelatoDesenvolvimentoResponse.de(useCase.registrar(criancaId, authentication.getName(), request.tipo(), request.descricao())); }
    @DeleteMapping("/{relatoId}") public void remover(@PathVariable UUID criancaId, @PathVariable UUID relatoId, Authentication authentication) { useCase.remover(criancaId, relatoId, authentication.getName()); }
}
