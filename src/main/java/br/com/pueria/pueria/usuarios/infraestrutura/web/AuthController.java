package br.com.pueria.pueria.usuarios.infraestrutura.web;

import br.com.pueria.pueria.usuarios.aplicacao.AutenticarUsuarioUseCase;
import br.com.pueria.pueria.usuarios.aplicacao.CadastrarUsuarioComando;
import br.com.pueria.pueria.usuarios.aplicacao.CadastrarUsuarioUseCase;
import br.com.pueria.pueria.usuarios.aplicacao.LoginComando;
import br.com.pueria.pueria.usuarios.aplicacao.SessaoAutenticacaoService;
import br.com.pueria.pueria.usuarios.dominio.UsuarioRepositorio;
import br.com.pueria.pueria.usuarios.aplicacao.UsuarioResumo;
import br.com.pueria.pueria.usuarios.aplicacao.SolicitarRedefinicaoSenhaUseCase;
import br.com.pueria.pueria.usuarios.aplicacao.RedefinirSenhaUseCase;
import br.com.pueria.pueria.comum.seguranca.IdentificadorCliente;
import br.com.pueria.pueria.comum.seguranca.LimiteRequisicoesExcedidoException;
import br.com.pueria.pueria.comum.seguranca.RateLimitResult;
import br.com.pueria.pueria.comum.seguranca.RateLimitService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CookieValue;
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
    private final UsuarioRepositorio usuarioRepositorio;
    private final SessaoAutenticacaoService sessaoAutenticacaoService;
    private final boolean cookieSeguro;
    private final boolean cookieParticionado;

    public AuthController(CadastrarUsuarioUseCase cadastrarUsuarioUseCase, AutenticarUsuarioUseCase autenticarUsuarioUseCase,
            SolicitarRedefinicaoSenhaUseCase solicitarRedefinicaoSenhaUseCase, RedefinirSenhaUseCase redefinirSenhaUseCase,
            RateLimitService rateLimitService, IdentificadorCliente identificadorCliente, UsuarioRepositorio usuarioRepositorio,
            SessaoAutenticacaoService sessaoAutenticacaoService,
            @Value("${seguranca.refresh-token.cookie-secure:true}") boolean cookieSeguro,
            @Value("${seguranca.refresh-token.cookie-partitioned:true}") boolean cookieParticionado) {
        this.cadastrarUsuarioUseCase = cadastrarUsuarioUseCase;
        this.autenticarUsuarioUseCase = autenticarUsuarioUseCase;
        this.solicitarRedefinicaoSenhaUseCase = solicitarRedefinicaoSenhaUseCase;
        this.redefinirSenhaUseCase = redefinirSenhaUseCase;
        this.rateLimitService = rateLimitService;
        this.identificadorCliente = identificadorCliente;
        this.usuarioRepositorio = usuarioRepositorio;
        this.sessaoAutenticacaoService = sessaoAutenticacaoService;
        this.cookieSeguro = cookieSeguro;
        this.cookieParticionado = cookieParticionado;
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
        autenticarUsuarioUseCase.executar(new LoginComando(request.email(), request.senha()));
        var usuario = usuarioRepositorio.buscarPorEmail(request.email()).orElseThrow();
        var credenciais = sessaoAutenticacaoService.criar(usuario);
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookieRefresh(credenciais.refreshToken(), sessaoAutenticacaoService.refreshExpiracaoSegundos()).toString()).body(AuthResponse.de(credenciais.accessToken()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@CookieValue(name = "pueria_refresh", defaultValue = "") String refreshToken) {
        if (refreshToken.isBlank()) {
            return ResponseEntity.status(401).build();
        }
        var credenciais = sessaoAutenticacaoService.renovar(refreshToken);
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookieRefresh(credenciais.refreshToken(), sessaoAutenticacaoService.refreshExpiracaoSegundos()).toString()).body(AuthResponse.de(credenciais.accessToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@CookieValue(name = "pueria_refresh", defaultValue = "") String refreshToken) {
        if (!refreshToken.isBlank()) {
            sessaoAutenticacaoService.revogar(refreshToken);
        }
        return ResponseEntity.noContent().header(HttpHeaders.SET_COOKIE, cookieRefresh("", 0).toString()).build();
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

    private ResponseCookie cookieRefresh(String valor, long maxAgeSegundos) {
        return ResponseCookie.from("pueria_refresh", valor)
                .httpOnly(true)
                .secure(cookieSeguro)
                .partitioned(cookieSeguro && cookieParticionado)
                .sameSite(cookieSeguro ? "None" : "Lax")
                .path("/api/auth")
                .maxAge(maxAgeSegundos)
                .build();
    }
}
