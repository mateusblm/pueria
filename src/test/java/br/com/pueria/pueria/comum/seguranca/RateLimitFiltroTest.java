package br.com.pueria.pueria.comum.seguranca;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "pueria.rate-limit.habilitado=true",
        "pueria.rate-limit.confiar-cabecalhos-encaminhados=true"
})
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RateLimitFiltroTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deveLimitarTentativasDeLoginPorIp() throws Exception {
        for (int tentativa = 0; tentativa < 5; tentativa++) {
            mockMvc.perform(post("/api/auth/login")
                            .header("X-Forwarded-For", "198.51.100.10")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"email\":\"invalido\",\"senha\":\"curta\"}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(header().string("RateLimit-Remaining", String.valueOf(4 - tentativa)));
        }

        mockMvc.perform(post("/api/auth/login")
                        .header("X-Forwarded-For", "198.51.100.10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"invalido\",\"senha\":\"curta\"}"))
                .andExpect(status().isTooManyRequests())
                .andExpect(header().exists("Retry-After"))
                .andExpect(jsonPath("$.erro").value("Limite de requisições excedido"));
    }

    @Test
    void deveLimitarRecuperacaoDeSenhaPorEmailMesmoComIpsDiferentes() throws Exception {
        for (int tentativa = 1; tentativa <= 3; tentativa++) {
            mockMvc.perform(post("/api/auth/recuperar-senha")
                            .header("X-Forwarded-For", "203.0.113." + tentativa)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"email\":\"familia.rate-limit@email.com\"}"))
                    .andExpect(status().isNoContent());
        }

        mockMvc.perform(post("/api/auth/recuperar-senha")
                        .header("X-Forwarded-For", "203.0.113.4")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"familia.rate-limit@email.com\"}"))
                .andExpect(status().isTooManyRequests())
                .andExpect(header().exists("Retry-After"))
                .andExpect(jsonPath("$.erro").value("Limite de requisições excedido"));
    }
}
