package br.com.pueria.pueria.home.infraestrutura.web;

import br.com.pueria.pueria.home.aplicacao.ResumoHomeService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/criancas/{criancaId}/home")
public class HomeController {
    private final ResumoHomeService resumoHome;

    public HomeController(ResumoHomeService resumoHome) { this.resumoHome = resumoHome; }

    @GetMapping("/resumo")
    public ResumoHomeResponse resumo(@PathVariable UUID criancaId, Authentication authentication) {
        return resumoHome.executar(criancaId, authentication.getName());
    }
}
