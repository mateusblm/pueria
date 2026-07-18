package br.com.pueria.pueria.comum.seguranca;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;

@Component
public class RateLimitFiltro extends OncePerRequestFilter {

    private static final Duration UM_MINUTO = Duration.ofMinutes(1);
    private static final Duration UMA_HORA = Duration.ofHours(1);

    private final RateLimitService rateLimitService;
    private final IdentificadorCliente identificadorCliente;
    private final boolean habilitado;

    public RateLimitFiltro(
            RateLimitService rateLimitService,
            IdentificadorCliente identificadorCliente,
            @Value("${pueria.rate-limit.habilitado:true}") boolean habilitado
    ) {
        this.rateLimitService = rateLimitService;
        this.identificadorCliente = identificadorCliente;
        this.habilitado = habilitado;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (!habilitado || HttpMethod.OPTIONS.matches(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        RateLimitResult resultado = limitePara(request);
        if (resultado != null && !resultado.permitido()) {
            responderLimiteExcedido(response, resultado);
            return;
        }
        if (resultado != null) {
            response.setHeader("RateLimit-Limit", String.valueOf(resultado.limite()));
            response.setHeader("RateLimit-Remaining", String.valueOf(resultado.restantes()));
        }

        filterChain.doFilter(request, response);
    }

    private RateLimitResult limitePara(HttpServletRequest request) {
        String caminho = request.getRequestURI().substring(request.getContextPath().length());
        boolean post = HttpMethod.POST.matches(request.getMethod());

        if (post && caminho.equals("/api/auth/login")) {
            return consumir("login:ip:" + identificadorCliente.porIp(request), 5, UM_MINUTO);
        }
        if (post && (caminho.equals("/api/auth/cadastro") || caminho.equals("/api/usuarios"))) {
            return consumir("cadastro:ip:" + identificadorCliente.porIp(request), 5, UMA_HORA);
        }
        if (post && (caminho.equals("/api/auth/recuperar-senha") || caminho.equals("/api/auth/redefinir-senha"))) {
            return consumir("senha:ip:" + identificadorCliente.porIp(request), 3, UMA_HORA);
        }
        if (post && caminho.equals("/api/auth/refresh")) {
            return consumir("refresh:sessao:" + identificadorCliente.porSessao(request), 30, UM_MINUTO);
        }

        Authentication autenticacao = SecurityContextHolder.getContext().getAuthentication();
        if (caminho.startsWith("/api/") && autenticacao != null && autenticacao.isAuthenticated()) {
            return consumir("api:usuario:" + identificadorCliente.porEmail(autenticacao.getName()), 200, UM_MINUTO);
        }
        return null;
    }

    private RateLimitResult consumir(String chave, int limite, Duration janela) {
        return rateLimitService.consumir(chave, limite, janela);
    }

    private void responderLimiteExcedido(HttpServletResponse response, RateLimitResult resultado) throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Retry-After", String.valueOf(resultado.retryAfterSegundos()));
        response.setHeader("RateLimit-Limit", String.valueOf(resultado.limite()));
        response.setHeader("RateLimit-Remaining", "0");
        response.getWriter().write("""
                {"status":429,"erro":"Limite de requisições excedido","mensagens":["Muitas tentativas. Aguarde um instante antes de tentar novamente."]}
                """);
    }
}
