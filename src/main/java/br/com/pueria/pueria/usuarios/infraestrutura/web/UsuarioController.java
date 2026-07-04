package br.com.pueria.pueria.usuarios.infraestrutura.web;

import br.com.pueria.pueria.usuarios.aplicacao.BuscarUsuarioAtualUseCase;
import br.com.pueria.pueria.usuarios.aplicacao.UsuarioResumo;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final BuscarUsuarioAtualUseCase buscarUsuarioAtualUseCase;

    public UsuarioController(BuscarUsuarioAtualUseCase buscarUsuarioAtualUseCase) {
        this.buscarUsuarioAtualUseCase = buscarUsuarioAtualUseCase;
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponse> me(Authentication authentication) {
        UsuarioResumo usuario = buscarUsuarioAtualUseCase.executar(authentication.getName());
        return ResponseEntity.ok(UsuarioResponse.de(usuario));
    }
}
