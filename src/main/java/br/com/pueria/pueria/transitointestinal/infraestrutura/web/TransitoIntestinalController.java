package br.com.pueria.pueria.transitointestinal.infraestrutura.web;

import br.com.pueria.pueria.transitointestinal.aplicacao.AtualizarTransitoIntestinalUseCase;
import br.com.pueria.pueria.transitointestinal.aplicacao.ListarTransitoIntestinalUseCase;
import br.com.pueria.pueria.transitointestinal.aplicacao.RegistrarTransitoIntestinalUseCase;
import br.com.pueria.pueria.transitointestinal.aplicacao.RegistroTransitoIntestinalDetalhado;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/criancas/{criancaId}/transito-intestinal/registros")
public class TransitoIntestinalController {

    private final ListarTransitoIntestinalUseCase listarUseCase;
    private final RegistrarTransitoIntestinalUseCase registrarUseCase;
    private final AtualizarTransitoIntestinalUseCase atualizarUseCase;

    public TransitoIntestinalController(ListarTransitoIntestinalUseCase listarUseCase, RegistrarTransitoIntestinalUseCase registrarUseCase, AtualizarTransitoIntestinalUseCase atualizarUseCase) {
        this.listarUseCase = listarUseCase;
        this.registrarUseCase = registrarUseCase;
        this.atualizarUseCase = atualizarUseCase;
    }

    @GetMapping
    public List<RegistroTransitoIntestinalResponse> listar(@PathVariable UUID criancaId, Authentication authentication) {
        return listarUseCase.executar(criancaId, authentication.getName())
                .stream()
                .map(RegistroTransitoIntestinalResponse::de)
                .toList();
    }

    @PostMapping
    public ResponseEntity<RegistroTransitoIntestinalResponse> registrar(
            @PathVariable UUID criancaId,
            @Valid @RequestBody TransitoIntestinalRequest request,
            Authentication authentication
    ) {
        RegistroTransitoIntestinalDetalhado registro = registrarUseCase.executar(request.paraRegistrar(criancaId, authentication.getName()));
        return ResponseEntity
                .created(URI.create("/api/criancas/" + criancaId + "/transito-intestinal/registros/" + registro.registro().getId()))
                .body(RegistroTransitoIntestinalResponse.de(registro));
    }

    @PutMapping("/{registroId}")
    public RegistroTransitoIntestinalResponse atualizar(
            @PathVariable UUID criancaId,
            @PathVariable UUID registroId,
            @Valid @RequestBody TransitoIntestinalRequest request,
            Authentication authentication
    ) {
        return RegistroTransitoIntestinalResponse.de(atualizarUseCase.executar(request.paraAtualizar(criancaId, registroId, authentication.getName())));
    }
}
