package br.com.pueria.pueria.configuracao;

import br.com.pueria.pueria.comum.excecao.RecursoNaoEncontradoException;
import br.com.pueria.pueria.responsaveis.dominio.VinculoResponsavelCriancaRepositorio;
import br.com.pueria.pueria.usuarios.dominio.Usuario;
import br.com.pueria.pueria.usuarios.dominio.UsuarioRepositorio;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;
import java.util.UUID;

@Component
public class AutorizacaoCriancaInterceptor implements HandlerInterceptor {

    private final UsuarioRepositorio usuarios;
    private final VinculoResponsavelCriancaRepositorio vinculos;

    public AutorizacaoCriancaInterceptor(
            UsuarioRepositorio usuarios,
            VinculoResponsavelCriancaRepositorio vinculos
    ) {
        this.usuarios = usuarios;
        this.vinculos = vinculos;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        Map<String, String> variaveis = variaveisDaRota(request);
        String idCrianca = variaveis.getOrDefault("criancaId", variaveis.get("id"));

        if (idCrianca == null) {
            return true;
        }

        UUID criancaId = parseIdCrianca(idCrianca);
        String email = request.getUserPrincipal() == null ? null : request.getUserPrincipal().getName();
        Usuario usuario = usuarios.buscarPorEmail(email)
                .filter(Usuario::isAtivo)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Criança não encontrada."));

        if (!vinculos.usuarioPodeAcessarCrianca(usuario.getId(), criancaId)) {
            throw new RecursoNaoEncontradoException("Criança não encontrada.");
        }

        return true;
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> variaveisDaRota(HttpServletRequest request) {
        Object atributo = request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        if (atributo instanceof Map<?, ?> mapa) {
            return (Map<String, String>) mapa;
        }
        return Map.of();
    }

    private UUID parseIdCrianca(String idCrianca) {
        try {
            return UUID.fromString(idCrianca);
        } catch (IllegalArgumentException ex) {
            throw new RecursoNaoEncontradoException("Criança não encontrada.");
        }
    }
}
