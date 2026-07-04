package br.com.pueria.pueria.usuarios.infraestrutura.seguranca;

import br.com.pueria.pueria.usuarios.dominio.Usuario;
import br.com.pueria.pueria.usuarios.dominio.UsuarioRepositorio;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class AutenticacaoJwtFiltro extends OncePerRequestFilter {

    private final TokenJwtService tokenJwtService;
    private final UsuarioRepositorio usuarioRepositorio;

    public AutenticacaoJwtFiltro(TokenJwtService tokenJwtService, UsuarioRepositorio usuarioRepositorio) {
        this.tokenJwtService = tokenJwtService;
        this.usuarioRepositorio = usuarioRepositorio;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = authorization.substring(7);
            String email = tokenJwtService.validarEObterEmail(token);

            usuarioRepositorio.buscarPorEmail(email)
                    .filter(Usuario::isAtivo)
                    .ifPresent(usuario -> {
                        List<SimpleGrantedAuthority> authorities = List.of(
                                new SimpleGrantedAuthority("ROLE_" + usuario.getTipo().name())
                        );

                        UsernamePasswordAuthenticationToken autenticacao = new UsernamePasswordAuthenticationToken(
                                usuario.getEmail(),
                                null,
                                authorities
                        );

                        SecurityContextHolder.getContext().setAuthentication(autenticacao);
                    });

            filterChain.doFilter(request, response);
        } catch (RuntimeException ex) {
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
