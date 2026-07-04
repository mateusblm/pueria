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

import static org.hamcrest.Matchers.blankOrNullString;
import static org.hamcrest.Matchers.not;
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
    void deveCriarCriancaComResponsavelAutenticadoEConsentimento() throws Exception {
        cadastrarUsuario("Mateus", "mateus.crianca@email.com", "senha123");
        String token = obterToken("mateus.crianca@email.com", "senha123");

        String body = """
                {
                  "nome": "Ana Clara",
                  "dataNascimento": "2024-01-10",
                  "sexo": "FEMININO",
                  "prematura": false,
                  "semanasGestacionais": 39,
                  "pesoNascimentoGramas": 3200,
                  "parentesco": "PAI",
                  "aceiteConsentimento": true,
                  "versaoTermoConsentimento": "2026.07"
                }
                """;

        mockMvc.perform(post("/api/criancas")
                        .header("Authorization", "Bearer " + token)
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
    void naoDeveCriarCriancaSemToken() throws Exception {
        String body = """
                {
                  "nome": "Ana Clara",
                  "dataNascimento": "2024-01-10",
                  "sexo": "FEMININO",
                  "prematura": false,
                  "semanasGestacionais": 39,
                  "pesoNascimentoGramas": 3200,
                  "parentesco": "PAI",
                  "aceiteConsentimento": true,
                  "versaoTermoConsentimento": "2026.07"
                }
                """;

        mockMvc.perform(post("/api/criancas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void naoDeveCriarCriancaSemConsentimentoAceito() throws Exception {
        cadastrarUsuario("Mateus", "mateus.semconsentimento@email.com", "senha123");
        String token = obterToken("mateus.semconsentimento@email.com", "senha123");

        String body = """
                {
                  "nome": "Ana Clara",
                  "dataNascimento": "2024-01-10",
                  "sexo": "FEMININO",
                  "prematura": false,
                  "semanasGestacionais": 39,
                  "pesoNascimentoGramas": 3200,
                  "parentesco": "PAI",
                  "aceiteConsentimento": false,
                  "versaoTermoConsentimento": "2026.07"
                }
                """;

        mockMvc.perform(post("/api/criancas")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("Regra de domínio violada"));
    }

    @Test
    void naoDeveCriarCriancaComNomeVazio() throws Exception {
        cadastrarUsuario("Mateus", "mateus.nomevazio@email.com", "senha123");
        String token = obterToken("mateus.nomevazio@email.com", "senha123");

        String body = """
                {
                  "nome": " ",
                  "dataNascimento": "2024-01-10",
                  "sexo": "FEMININO",
                  "prematura": false,
                  "semanasGestacionais": 39,
                  "pesoNascimentoGramas": 3200,
                  "parentesco": "PAI",
                  "aceiteConsentimento": true,
                  "versaoTermoConsentimento": "2026.07"
                }
                """;

        mockMvc.perform(post("/api/criancas")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("Dados inválidos"));
    }

    @Test
    void deveRetornarNotFoundQuandoCriancaNaoExisteParaResponsavelAutenticado() throws Exception {
        cadastrarUsuario("Mateus", "mateus.notfound@email.com", "senha123");
        String token = obterToken("mateus.notfound@email.com", "senha123");

        mockMvc.perform(get("/api/criancas/{id}", UUID.randomUUID())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").value("Recurso não encontrado"));
    }

    private void cadastrarUsuario(String nome, String email, String senha) throws Exception {
        mockMvc.perform(post("/api/auth/cadastro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "%s",
                                  "email": "%s",
                                  "senha": "%s"
                                }
                                """.formatted(nome, email, senha)))
                .andExpect(status().isCreated());
    }

    private String obterToken(String email, String senha) throws Exception {
        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "%s",
                                  "senha": "%s"
                                }
                                """.formatted(email, senha)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", not(blankOrNullString())))
                .andReturn()
                .getResponse()
                .getContentAsString();

        int inicio = response.indexOf("\"token\":\"") + 9;
        int fim = response.indexOf("\"", inicio);
        return response.substring(inicio, fim);
    }
}
