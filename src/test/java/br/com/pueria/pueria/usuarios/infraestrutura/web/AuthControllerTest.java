package br.com.pueria.pueria.usuarios.infraestrutura.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.blankOrNullString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deveCadastrarUsuario() throws Exception {
        mockMvc.perform(post("/api/auth/cadastro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Mateus",
                                  "email": "mateus.cadastro@email.com",
                                  "senha": "senha123"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value("Mateus"))
                .andExpect(jsonPath("$.email").value("mateus.cadastro@email.com"))
                .andExpect(jsonPath("$.tipo").value("RESPONSAVEL"));
    }

    @Test
    void naoDeveCadastrarUsuarioComEmailDuplicado() throws Exception {
        String body = """
                {
                  "nome": "Mateus",
                  "email": "mateus.duplicado@email.com",
                  "senha": "senha123"
                }
                """;

        mockMvc.perform(post("/api/auth/cadastro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/auth/cadastro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("Regra de domínio violada"));
    }

    @Test
    void deveAutenticarUsuarioERetornarToken() throws Exception {
        cadastrarUsuario("Mateus", "mateus.login@email.com", "senha123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "mateus.login@email.com",
                                  "senha": "senha123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipo").value("Bearer"))
                .andExpect(jsonPath("$.token", not(blankOrNullString())))
                .andExpect(jsonPath("$.expiraEmSegundos").value(3600));
    }

    @Test
    void naoDeveAutenticarUsuarioComSenhaIncorreta() throws Exception {
        cadastrarUsuario("Mateus", "mateus.senhaerrada@email.com", "senha123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "mateus.senhaerrada@email.com",
                                  "senha": "senha-errada"
                                }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.erro").value("Credenciais inválidas"));
    }

    @Test
    void deveRetornarUsuarioAtualComTokenValido() throws Exception {
        cadastrarUsuario("Mateus", "mateus.me@email.com", "senha123");
        String token = obterToken("mateus.me@email.com", "senha123");

        mockMvc.perform(get("/api/usuarios/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Mateus"))
                .andExpect(jsonPath("$.email").value("mateus.me@email.com"))
                .andExpect(jsonPath("$.tipo").value("RESPONSAVEL"));
    }

    @Test
    void naoDeveAcessarUsuarioAtualSemToken() throws Exception {
        mockMvc.perform(get("/api/usuarios/me"))
                .andExpect(status().isUnauthorized());
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
                .andReturn()
                .getResponse()
                .getContentAsString();

        int inicio = response.indexOf("\"token\":\"") + 9;
        int fim = response.indexOf("\"", inicio);
        return response.substring(inicio, fim);
    }
}
