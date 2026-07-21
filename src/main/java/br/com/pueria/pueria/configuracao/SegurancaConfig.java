package br.com.pueria.pueria.configuracao;

import br.com.pueria.pueria.usuarios.infraestrutura.seguranca.AutenticacaoJwtFiltro;
import br.com.pueria.pueria.comum.seguranca.RateLimitFiltro;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class SegurancaConfig {

    private final List<String> allowedOrigins;

    public SegurancaConfig(@Value("${pueria.cors.allowed-origins:http://localhost:4200}") String allowedOrigins) {
        this.allowedOrigins = Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .map(origem -> origem.replaceAll("/+$", ""))
                .filter(origem -> !origem.isBlank())
                .toList();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            AutenticacaoJwtFiltro autenticacaoJwtFiltro,
            RateLimitFiltro rateLimitFiltro,
            ValidacaoOrigemAuthFiltro validacaoOrigemAuthFiltro
    ) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {})
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(excecoes -> excecoes
                        .authenticationEntryPoint((request, response, exception) ->
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED)
                        )
                        .accessDeniedHandler((request, response, exception) ->
                                response.sendError(HttpServletResponse.SC_FORBIDDEN)
                        )
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(
                                "/api/auth/cadastro",
                                "/api/auth/login",
                                "/api/auth/refresh",
                                "/api/auth/logout",
                                "/api/auth/recuperar-senha",
                                "/api/auth/redefinir-senha",
                                "/api/status",
                                "/api/status/sentry-test",
                                "/actuator/health"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(validacaoOrigemAuthFiltro, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(autenticacaoJwtFiltro, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(rateLimitFiltro, AutenticacaoJwtFiltro.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(allowedOrigins);
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "Origin"));
        configuration.setExposedHeaders(List.of("Location"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration
    ) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
