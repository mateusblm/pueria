package br.com.pueria.pueria.criancas.infraestrutura.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CriancaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deveCriarCrianca() throws Exception {
        String body = """
                {
                  "nome": "Ana Clara",
                  "dataNascimento": "2024-01-10",
                  "sexo": "FEMININO",
                  "prematura": false,
                  "semanasGestacionais": 39,
                  "pesoNascimentoGramas": 3200
                }
                """;

        mockMvc.perform(post("/api/criancas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value("Ana Clara"))
                .andExpect(jsonPath("$.sexo").value("FEMININO"))
                .andExpect(jsonPath("$.prematura").value(false));
    }

    @Test
    void naoDeveCriarCriancaComNomeVazio() throws Exception {
        String body = """
                {
                  "nome": " ",
                  "dataNascimento": "2024-01-10",
                  "sexo": "FEMININO",
                  "prematura": false,
                  "semanasGestacionais": 39,
                  "pesoNascimentoGramas": 3200
                }
                """;

        mockMvc.perform(post("/api/criancas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("Dados inválidos"));
    }

    @Test
    void deveRetornarNotFoundQuandoCriancaNaoExiste() throws Exception {
        mockMvc.perform(get("/api/criancas/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").value("Recurso não encontrado"));
    }
}
