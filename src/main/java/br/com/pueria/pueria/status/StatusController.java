package br.com.pueria.pueria.status;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatusController {

    @GetMapping("/api/status")
    public StatusResponse status() {
        return new StatusResponse("Pueria API em execução");
    }

    public record StatusResponse(String mensagem) {
    }
}
