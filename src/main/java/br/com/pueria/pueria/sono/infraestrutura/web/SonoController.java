package br.com.pueria.pueria.sono.infraestrutura.web;

import br.com.pueria.pueria.sono.aplicacao.AtualizarSonoUseCase;
import br.com.pueria.pueria.sono.aplicacao.ListarSonoUseCase;
import br.com.pueria.pueria.sono.aplicacao.RegistrarSonoUseCase;
import br.com.pueria.pueria.sono.aplicacao.RegistroSonoDetalhado;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/criancas/{criancaId}/sono/registros")
public class SonoController {

    private final ListarSonoUseCase listarUseCase;
    private final RegistrarSonoUseCase registrarUseCase;
    private final AtualizarSonoUseCase atualizarUseCase;

    public SonoController(ListarSonoUseCase listarUseCase, RegistrarSonoUseCase registrarUseCase, AtualizarSonoUseCase atualizarUseCase) {
        this.listarUseCase = listarUseCase;
        this.registrarUseCase = registrarUseCase;
        this.atualizarUseCase = atualizarUseCase;
    }

    @GetMapping
    public List<RegistroSonoResponse> listar(@PathVariable UUID criancaId, Authentication authentication) {
        return listarUseCase.executar(criancaId, authentication.getName())
                .stream()
                .map(RegistroSonoResponse::de)
                .toList();
    }

    @PostMapping
    public ResponseEntity<RegistroSonoResponse> registrar(
            @PathVariable UUID criancaId,
            @Valid @RequestBody SonoRequest request,
            Authentication authentication
    ) {
        RegistroSonoDetalhado registro = registrarUseCase.executar(request.paraRegistrar(criancaId, authentication.getName()));
        return ResponseEntity
                .created(URI.create("/api/criancas/" + criancaId + "/sono/registros/" + registro.registro().getId()))
                .body(RegistroSonoResponse.de(registro));
    }

    @PutMapping("/{registroId}")
    public RegistroSonoResponse atualizar(
            @PathVariable UUID criancaId,
            @PathVariable UUID registroId,
            @Valid @RequestBody SonoRequest request,
            Authentication authentication
    ) {
        return RegistroSonoResponse.de(atualizarUseCase.executar(request.paraAtualizar(criancaId, registroId, authentication.getName())));
    }
}
