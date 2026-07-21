package br.com.pueria.pueria.status;

import io.sentry.Sentry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatusController {

    @GetMapping("/api/status")
    public StatusResponse status() {
        return new StatusResponse("Pueria API em execução");
    }

    // Endpoint temporário: remover após validar o primeiro evento no Sentry.
    @GetMapping("/api/status/sentry-test")
    public void sentryTest() {
        try {
            throw new Exception("Sentry smoke test: exceção intencional");
        } catch (Exception exception) {
            Sentry.captureException(exception);
            throw new IllegalStateException("Sentry smoke test: exceção intencional", exception);
        }
    }

    public record StatusResponse(String mensagem) {
    }
}
