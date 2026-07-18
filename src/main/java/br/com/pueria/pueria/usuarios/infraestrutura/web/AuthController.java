package br.com.pueria.pueria.usuarios.infraestrutura.web;

import br.com.pueria.pueria.usuarios.aplicacao.AutenticarUsuarioUseCase;
import br.com.pueria.pueria.usuarios.aplicacao.CadastrarUsuarioComando;
import br.com.pueria.pueria.usuarios.aplicacao.CadastrarUsuarioUseCase;
import br.com.pueria.pueria.usuarios.aplicacao.LoginComando;
import br.com.pueria.pueria.usuarios.aplicacao.TokenAutenticacao;
import br.com.pueria.pueria.usuarios.aplicacao.UsuarioResumo;
import br.com.pueria.pueria.usuarios.aplicacao.SolicitarRedefinicaoSenhaUseCase;
import br.com.pueria.pueria.usuarios.aplicacao.RedefinirSenhaUseCase;
import br.com.pueria.pueria.comum.seguranca.IdentificadorCliente;
import br.com.pueria.pueria.comum.seguranca.LimiteRequisicoesExcedidoException;
import br.com.pueria.pueria.comum.seguranca.RateLimitResult;
import br.com.pueria.pueria.comum.seguranca.RateLimitService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.Duration;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final CadastrarUsuarioUseCase cadastrarUsuarioUseCase;
    private final AutenticarUsuarioUseCase autenticarUsuarioUseCase;
    private final SolicitarRedefinicaoSenhaUseCase solicitarRedefinicaoSenhaUseCase;
    private final RedefinirSenhaUseCase redefinirSenhaUseCase;
    private final RateLimitService rateLimitService;
    private final IdentificadorCliente identificadorCliente;

    public AuthController(CadastrarUsuarioUseCase cadastrarUsuarioUseCase, AutenticarUsuarioUseCase autenticarUsuarioUseCase,
            SolicitarRedefinicaoSenhaUseCase solicitarRedefinicaoSenhaUseCase, RedefinirSenhaUseCase redefinirSenhaUseCase,
            RateLimitService rateLimitService, IdentificadorCliente identificadorCliente) {
        this.cadastrarUsuarioUseCase = cadastrarUsuarioUseCase;
        this.autenticarUsuarioUseCase = autenticarUsuarioUseCase;
        this.solicitarRedefinicaoSenhaUseCase = solicitarRedefinicaoSenhaUseCase;
        this.redefinirSenhaUseCase = redefinirSenhaUseCase;
        this.rateLimitService = rateLimitService;
        this.identificadorCliente = identificadorCliente;
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

    @PostMapping("/recuperar-senha")
    public ResponseEntity<Void> solicitarRedefinicao(@RequestBody @Valid SolicitarRedefinicaoSenhaRequest request) {
        RateLimitResult resultado = rateLimitService.consumir(
                "senha:email:" + identificadorCliente.porEmail(request.email()),
                3,
                Duration.ofHours(1)
        );
        if (!resultado.permitido()) {
            throw new LimiteRequisicoesExcedidoException(resultado.retryAfterSegundos());
        }
        solicitarRedefinicaoSenhaUseCase.executar(request.email());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/redefinir-senha")
    public ResponseEntity<Void> redefinirSenha(@RequestBody @Valid RedefinirSenhaRequest request) {
        redefinirSenhaUseCase.executar(request.token(), request.novaSenha());
        return ResponseEntity.noContent().build();
    }
}
