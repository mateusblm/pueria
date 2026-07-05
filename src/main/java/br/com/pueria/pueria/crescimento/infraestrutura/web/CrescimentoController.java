package br.com.pueria.pueria.crescimento.infraestrutura.web;

import br.com.pueria.pueria.crescimento.aplicacao.AtualizarMedidaCrescimentoUseCase;
import br.com.pueria.pueria.crescimento.aplicacao.ListarMedidasCrescimentoUseCase;
import br.com.pueria.pueria.crescimento.aplicacao.RegistrarMedidaCrescimentoUseCase;
import br.com.pueria.pueria.crescimento.aplicacao.RemoverMedidaCrescimentoUseCase;
import br.com.pueria.pueria.crescimento.dominio.MedidaCrescimento;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/criancas/{criancaId}/crescimento/medidas")
public class CrescimentoController {

    private final ListarMedidasCrescimentoUseCase listarUseCase;
    private final RegistrarMedidaCrescimentoUseCase registrarUseCase;
    private final AtualizarMedidaCrescimentoUseCase atualizarUseCase;
    private final RemoverMedidaCrescimentoUseCase removerUseCase;

    public CrescimentoController(ListarMedidasCrescimentoUseCase listarUseCase, RegistrarMedidaCrescimentoUseCase registrarUseCase, AtualizarMedidaCrescimentoUseCase atualizarUseCase, RemoverMedidaCrescimentoUseCase removerUseCase) {
        this.listarUseCase = listarUseCase;
        this.registrarUseCase = registrarUseCase;
        this.atualizarUseCase = atualizarUseCase;
        this.removerUseCase = removerUseCase;
    }

    @GetMapping
    public List<MedidaCrescimentoResponse> listar(@PathVariable UUID criancaId, Authentication authentication) {
        return listarUseCase.executar(criancaId, authentication.getName())
                .stream()
                .map(MedidaCrescimentoResponse::de)
                .toList();
    }

    @PostMapping
    public ResponseEntity<MedidaCrescimentoResponse> registrar(
            @PathVariable UUID criancaId,
            @Valid @RequestBody MedidaCrescimentoRequest request,
            Authentication authentication
    ) {
        MedidaCrescimento medida = registrarUseCase.executar(request.paraRegistrar(criancaId, authentication.getName()));
        return ResponseEntity
                .created(URI.create("/api/criancas/" + criancaId + "/crescimento/medidas/" + medida.getId()))
                .body(MedidaCrescimentoResponse.de(medida));
    }

    @PutMapping("/{medidaId}")
    public MedidaCrescimentoResponse atualizar(
            @PathVariable UUID criancaId,
            @PathVariable UUID medidaId,
            @Valid @RequestBody MedidaCrescimentoRequest request,
            Authentication authentication
    ) {
        return MedidaCrescimentoResponse.de(atualizarUseCase.executar(request.paraAtualizar(criancaId, medidaId, authentication.getName())));
    }

    @DeleteMapping("/{medidaId}")
    public ResponseEntity<Void> remover(@PathVariable UUID criancaId, @PathVariable UUID medidaId, Authentication authentication) {
        removerUseCase.executar(criancaId, medidaId, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
