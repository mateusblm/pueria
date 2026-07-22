package br.com.pueria.pueria.responsaveis.infraestrutura.web;

import br.com.pueria.pueria.responsaveis.aplicacao.GerenciarCuidadoresUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.UUID;

@RestController @RequestMapping("/api/convites-cuidadores")
public class ConvitesCuidadoresController {
    private final GerenciarCuidadoresUseCase cuidadores;
    public ConvitesCuidadoresController(GerenciarCuidadoresUseCase cuidadores) { this.cuidadores = cuidadores; }
    @GetMapping public List<ConviteCuidadorResponse> listar(Authentication auth) { return cuidadores.listarConvitesPendentes(auth.getName()).stream().map(ConviteCuidadorResponse::de).toList(); }
    @PostMapping("/{conviteId}/aceitar") public ResponseEntity<Void> aceitar(@PathVariable UUID conviteId, Authentication auth) { cuidadores.responderConvite(conviteId, auth.getName(), true); return ResponseEntity.noContent().build(); }
    @PostMapping("/{conviteId}/recusar") public ResponseEntity<Void> recusar(@PathVariable UUID conviteId, Authentication auth) { cuidadores.responderConvite(conviteId, auth.getName(), false); return ResponseEntity.noContent().build(); }
}
