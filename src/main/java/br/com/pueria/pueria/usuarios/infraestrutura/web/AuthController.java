package br.com.pueria.pueria.usuarios.infraestrutura.web;

import br.com.pueria.pueria.usuarios.aplicacao.AutenticarUsuarioUseCase;
import br.com.pueria.pueria.usuarios.aplicacao.CadastrarUsuarioComando;
import br.com.pueria.pueria.usuarios.aplicacao.CadastrarUsuarioUseCase;
import br.com.pueria.pueria.usuarios.aplicacao.LoginComando;
import br.com.pueria.pueria.usuarios.aplicacao.TokenAutenticacao;
import br.com.pueria.pueria.usuarios.aplicacao.UsuarioResumo;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final CadastrarUsuarioUseCase cadastrarUsuarioUseCase;
    private final AutenticarUsuarioUseCase autenticarUsuarioUseCase;

    public AuthController(CadastrarUsuarioUseCase cadastrarUsuarioUseCase, AutenticarUsuarioUseCase autenticarUsuarioUseCase) {
        this.cadastrarUsuarioUseCase = cadastrarUsuarioUseCase;
        this.autenticarUsuarioUseCase = autenticarUsuarioUseCase;
    }

    @PostMapping("/cadastro")
    public ResponseEntity<UsuarioResponse> cadastrar(
            @RequestBody @Valid CadastroUsuarioRequest request,
            UriComponentsBuilder uriBuilder
    ) {
        UsuarioResumo usuario = cadastrarUsuarioUseCase.executar(
                new CadastrarUsuarioComando(request.nome(), request.email(), request.senha())
        );

        URI location = uriBuilder.path("/api/usuarios/me").build().toUri();
        return ResponseEntity.created(location).body(UsuarioResponse.de(usuario));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest request) {
        TokenAutenticacao token = autenticarUsuarioUseCase.executar(new LoginComando(request.email(), request.senha()));
        return ResponseEntity.ok(AuthResponse.de(token));
    }
}
