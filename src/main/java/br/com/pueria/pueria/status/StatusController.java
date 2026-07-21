package br.com.pueria.pueria.status;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatusController {

    @GetMapping("/api/status")
    public StatusResponse status() {
        return new StatusResponse("Pueria API em execução");
    }

    // TODO: remover imediatamente após validar o primeiro evento no Sentry.
    @GetMapping("/api/status/sentry-test")
    public void sentryTest() {
        throw new IllegalStateException("Sentry smoke test: exceção intencional");
    }

    public record StatusResponse(String mensagem) {
    }
}
