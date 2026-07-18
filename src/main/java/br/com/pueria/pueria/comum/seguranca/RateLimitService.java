package br.com.pueria.pueria.comum.seguranca;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitService {

    private static final int MAXIMO_DE_JANELAS = 20_000;

    private final Map<String, Janela> janelas = new ConcurrentHashMap<>();

    public RateLimitResult consumir(String chave, int limite, Duration duracao) {
        if (chave == null || chave.isBlank() || limite < 1 || duracao.isNegative() || duracao.isZero()) {
            throw new IllegalArgumentException("Configuração de rate limit inválida.");
        }

        limparJanelasExpiradasQuandoNecessario();
        Instant agora = Instant.now();
        RateLimitResult[] resultado = new RateLimitResult[1];

        janelas.compute(chave, (ignorada, janelaAtual) -> {
            Janela janela = janelaAtual == null || !agora.isBefore(janelaAtual.expiraEm)
                    ? new Janela(0, agora.plus(duracao))
                    : janelaAtual;

            if (janela.consumidos >= limite) {
                resultado[0] = RateLimitResult.bloqueado(limite, segundosAte(janela.expiraEm, agora));
                return janela;
            }

            janela.consumidos++;
            resultado[0] = RateLimitResult.permitido(limite, limite - janela.consumidos);
            return janela;
        });

        return resultado[0];
    }

    private void limparJanelasExpiradasQuandoNecessario() {
        if (janelas.size() < MAXIMO_DE_JANELAS) {
            return;
        }

        Instant agora = Instant.now();
        janelas.entrySet().removeIf(entry -> !agora.isBefore(entry.getValue().expiraEm));
    }

    private long segundosAte(Instant fim, Instant agora) {
        long milissegundos = Math.max(1, Duration.between(agora, fim).toMillis());
        return Math.max(1, (milissegundos + 999) / 1000);
    }

    private static final class Janela {
        private int consumidos;
        private final Instant expiraEm;

        private Janela(int consumidos, Instant expiraEm) {
            this.consumidos = consumidos;
            this.expiraEm = expiraEm;
        }
    }
}
