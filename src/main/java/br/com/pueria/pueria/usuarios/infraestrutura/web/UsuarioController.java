package br.com.pueria.pueria.usuarios.infraestrutura.web;

import br.com.pueria.pueria.usuarios.aplicacao.BuscarUsuarioAtualUseCase;
import br.com.pueria.pueria.usuarios.aplicacao.AtualizarCredenciaisUsuarioUseCase;
import br.com.pueria.pueria.usuarios.aplicacao.UsuarioResumo;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final BuscarUsuarioAtualUseCase buscarUsuarioAtualUseCase;
    private final AtualizarCredenciaisUsuarioUseCase atualizarCredenciais;

    public UsuarioController(BuscarUsuarioAtualUseCase buscarUsuarioAtualUseCase, AtualizarCredenciaisUsuarioUseCase atualizarCredenciais) {
        this.buscarUsuarioAtualUseCase = buscarUsuarioAtualUseCase;
        this.atualizarCredenciais = atualizarCredenciais;
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponse> me(Authentication authentication) {
        UsuarioResumo usuario = buscarUsuarioAtualUseCase.executar(authentication.getName());
        return ResponseEntity.ok(UsuarioResponse.de(usuario));
    }

    @PutMapping("/me/email") public ResponseEntity<Void> email(Authentication authentication, @RequestBody @jakarta.validation.Valid AtualizarEmailRequest request) { atualizarCredenciais.email(authentication.getName(), request.email(), request.senhaAtual()); return ResponseEntity.noContent().build(); }
    @PutMapping("/me/senha") public ResponseEntity<Void> senha(Authentication authentication, @RequestBody @jakarta.validation.Valid AtualizarSenhaRequest request) { atualizarCredenciais.senha(authentication.getName(), request.senhaAtual(), request.novaSenha()); return ResponseEntity.noContent().build(); }
}
