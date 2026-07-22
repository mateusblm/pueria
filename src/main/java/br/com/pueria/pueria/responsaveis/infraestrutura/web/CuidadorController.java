package br.com.pueria.pueria.responsaveis.infraestrutura.web;

import br.com.pueria.pueria.responsaveis.aplicacao.GerenciarCuidadoresUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api/criancas/{criancaId}/cuidadores")
public class CuidadorController {
    private final GerenciarCuidadoresUseCase cuidadores;
    public CuidadorController(GerenciarCuidadoresUseCase cuidadores) { this.cuidadores = cuidadores; }

    @GetMapping
    public List<CuidadorResponse> listar(@PathVariable UUID criancaId, Authentication authentication) {
        return cuidadores.listar(criancaId, authentication.getName()).stream().map(CuidadorResponse::de).toList();
    }

    @PostMapping
    public ResponseEntity<ConviteCuidadorResponse> convidar(@PathVariable UUID criancaId, @Valid @RequestBody ConvidarCuidadorRequest request, Authentication authentication) {
        return ResponseEntity.ok(ConviteCuidadorResponse.de(cuidadores.convidar(criancaId, authentication.getName(), request.email(), request.parentesco())));
    }

    @DeleteMapping("/{vinculoId}")
    public ResponseEntity<Void> remover(@PathVariable UUID criancaId, @PathVariable UUID vinculoId, Authentication authentication) {
        cuidadores.remover(criancaId, vinculoId, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
