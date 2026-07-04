package br.com.pueria.pueria.criancas.infraestrutura.web;

import br.com.pueria.pueria.criancas.aplicacao.AtualizarCriancaUseCase;
import br.com.pueria.pueria.criancas.aplicacao.BuscarCriancaUseCase;
import br.com.pueria.pueria.criancas.aplicacao.CriarCriancaUseCase;
import br.com.pueria.pueria.criancas.aplicacao.ListarCriancasUseCase;
import br.com.pueria.pueria.criancas.aplicacao.RemoverCriancaUseCase;
import br.com.pueria.pueria.criancas.dominio.Crianca;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/criancas")
public class CriancaController {

    private final CriarCriancaUseCase criarCriancaUseCase;
    private final BuscarCriancaUseCase buscarCriancaUseCase;
    private final ListarCriancasUseCase listarCriancasUseCase;
    private final AtualizarCriancaUseCase atualizarCriancaUseCase;
    private final RemoverCriancaUseCase removerCriancaUseCase;

    public CriancaController(
            CriarCriancaUseCase criarCriancaUseCase,
            BuscarCriancaUseCase buscarCriancaUseCase,
            ListarCriancasUseCase listarCriancasUseCase,
            AtualizarCriancaUseCase atualizarCriancaUseCase,
            RemoverCriancaUseCase removerCriancaUseCase
    ) {
        this.criarCriancaUseCase = criarCriancaUseCase;
        this.buscarCriancaUseCase = buscarCriancaUseCase;
        this.listarCriancasUseCase = listarCriancasUseCase;
        this.atualizarCriancaUseCase = atualizarCriancaUseCase;
        this.removerCriancaUseCase = removerCriancaUseCase;
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

    @GetMapping
    public List<CriancaResponse> listar(Authentication authentication) {
        return listarCriancasUseCase.executar(authentication.getName())
                .stream()
                .map(CriancaResponse::de)
                .toList();
    }

    @GetMapping("/{id}")
    public CriancaResponse buscarPorId(@PathVariable UUID id, Authentication authentication) {
        Crianca crianca = buscarCriancaUseCase.executar(id, authentication.getName());
        return CriancaResponse.de(crianca);
    }

    @PutMapping("/{id}")
    public CriancaResponse atualizar(
            @PathVariable UUID id,
            @Valid @RequestBody AtualizarCriancaRequest request,
            Authentication authentication
    ) {
        Crianca crianca = atualizarCriancaUseCase.executar(request.paraComando(id, authentication.getName()));
        return CriancaResponse.de(crianca);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable UUID id, Authentication authentication) {
        removerCriancaUseCase.executar(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
