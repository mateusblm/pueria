package br.com.pueria.pueria.status;

import io.sentry.Sentry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatusController {

    @GetMapping("/api/status")
    public StatusResponse status(@RequestParam(defaultValue = "false") boolean sentryTest) {
        if (sentryTest) {
            try {
                throw new Exception("Sentry smoke test: exceção intencional");
            } catch (Exception exception) {
                Sentry.captureException(exception);
                throw new IllegalStateException("Sentry smoke test: exceção intencional", exception);
            }
        }
        return new StatusResponse("Pueria API em execução");
    }

    public record StatusResponse(String mensagem) {
    }
}
