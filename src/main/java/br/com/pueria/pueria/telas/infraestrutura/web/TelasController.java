package br.com.pueria.pueria.telas.infraestrutura.web;

import br.com.pueria.pueria.telas.aplicacao.AtualizarTelasUseCase;
import br.com.pueria.pueria.telas.aplicacao.ListarTelasUseCase;
import br.com.pueria.pueria.telas.aplicacao.RegistrarTelasUseCase;
import br.com.pueria.pueria.telas.aplicacao.RegistroTelasDetalhado;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/criancas/{criancaId}/telas/registros")
public class TelasController {

    private final ListarTelasUseCase listarUseCase;
    private final RegistrarTelasUseCase registrarUseCase;
    private final AtualizarTelasUseCase atualizarUseCase;

    public TelasController(ListarTelasUseCase listarUseCase, RegistrarTelasUseCase registrarUseCase, AtualizarTelasUseCase atualizarUseCase) {
        this.listarUseCase = listarUseCase;
        this.registrarUseCase = registrarUseCase;
        this.atualizarUseCase = atualizarUseCase;
    }

    @GetMapping
    public List<RegistroTelasResponse> listar(@PathVariable UUID criancaId, Authentication authentication) {
        return listarUseCase.executar(criancaId, authentication.getName())
                .stream()
                .map(RegistroTelasResponse::de)
                .toList();
    }

    @PostMapping
    public ResponseEntity<RegistroTelasResponse> registrar(
            @PathVariable UUID criancaId,
            @Valid @RequestBody TelasRequest request,
            Authentication authentication
    ) {
        RegistroTelasDetalhado registro = registrarUseCase.executar(request.paraRegistrar(criancaId, authentication.getName()));
        return ResponseEntity
                .created(URI.create("/api/criancas/" + criancaId + "/telas/registros/" + registro.registro().getId()))
                .body(RegistroTelasResponse.de(registro));
    }

    @PutMapping("/{registroId}")
    public RegistroTelasResponse atualizar(
            @PathVariable UUID criancaId,
            @PathVariable UUID registroId,
            @Valid @RequestBody TelasRequest request,
            Authentication authentication
    ) {
        return RegistroTelasResponse.de(atualizarUseCase.executar(request.paraAtualizar(criancaId, registroId, authentication.getName())));
    }
}
