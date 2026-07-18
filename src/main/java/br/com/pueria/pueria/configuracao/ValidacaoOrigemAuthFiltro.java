package br.com.pueria.pueria.configuracao;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class ValidacaoOrigemAuthFiltro extends OncePerRequestFilter {

    private final List<String> origensPermitidas;

    public ValidacaoOrigemAuthFiltro(@Value("${pueria.cors.allowed-origins:http://localhost:4200}") String allowedOrigins) {
        this.origensPermitidas = Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .map(origem -> origem.replaceAll("/+$", ""))
                .filter(origem -> !origem.isBlank())
                .toList();
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !"POST".equalsIgnoreCase(request.getMethod())
                || !("/api/auth/refresh".equals(request.getRequestURI())
                || "/api/auth/logout".equals(request.getRequestURI()));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String origin = request.getHeader("Origin");
        if (origin != null && !origensPermitidas.contains(origin.replaceAll("/+$", ""))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
