package br.com.pueria.pueria.alimentacao.infraestrutura.web;

import br.com.pueria.pueria.alimentacao.aplicacao.AtualizarAlimentacaoUseCase;
import br.com.pueria.pueria.alimentacao.aplicacao.ListarAlimentacaoUseCase;
import br.com.pueria.pueria.alimentacao.aplicacao.RegistrarAlimentacaoUseCase;
import br.com.pueria.pueria.alimentacao.aplicacao.RegistroAlimentacaoDetalhado;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/criancas/{criancaId}/alimentacao/registros")
public class AlimentacaoController {

    private final ListarAlimentacaoUseCase listarUseCase;
    private final RegistrarAlimentacaoUseCase registrarUseCase;
    private final AtualizarAlimentacaoUseCase atualizarUseCase;

    public AlimentacaoController(ListarAlimentacaoUseCase listarUseCase, RegistrarAlimentacaoUseCase registrarUseCase, AtualizarAlimentacaoUseCase atualizarUseCase) {
        this.listarUseCase = listarUseCase;
        this.registrarUseCase = registrarUseCase;
        this.atualizarUseCase = atualizarUseCase;
    }

    @GetMapping
    public List<RegistroAlimentacaoResponse> listar(@PathVariable UUID criancaId, Authentication authentication) {
        return listarUseCase.executar(criancaId, authentication.getName())
                .stream()
                .map(RegistroAlimentacaoResponse::de)
                .toList();
    }

    @PostMapping
    public ResponseEntity<RegistroAlimentacaoResponse> registrar(
            @PathVariable UUID criancaId,
            @Valid @RequestBody AlimentacaoRequest request,
            Authentication authentication
    ) {
        RegistroAlimentacaoDetalhado registro = registrarUseCase.executar(request.paraRegistrar(criancaId, authentication.getName()));
        return ResponseEntity
                .created(URI.create("/api/criancas/" + criancaId + "/alimentacao/registros/" + registro.registro().getId()))
                .body(RegistroAlimentacaoResponse.de(registro));
    }

    @PutMapping("/{registroId}")
    public RegistroAlimentacaoResponse atualizar(
            @PathVariable UUID criancaId,
            @PathVariable UUID registroId,
            @Valid @RequestBody AlimentacaoRequest request,
            Authentication authentication
    ) {
        return RegistroAlimentacaoResponse.de(atualizarUseCase.executar(request.paraAtualizar(criancaId, registroId, authentication.getName())));
    }
}
