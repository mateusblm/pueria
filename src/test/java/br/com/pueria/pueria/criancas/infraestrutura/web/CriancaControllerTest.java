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
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

        mockMvc.perform(post("/api/criancas")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(criancaJson("Ana Clara", 39, 3200, false)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value("Ana Clara"))
                .andExpect(jsonPath("$.sexo").value("FEMININO"))
                .andExpect(jsonPath("$.prematura").value(false))
                .andExpect(jsonPath("$.semanasGestacionais").value(39))
                .andExpect(jsonPath("$.diasGestacionais").value(0))
                .andExpect(jsonPath("$.tipoParto").value("VAGINAL"))
                .andExpect(jsonPath("$.pesoNascimentoGramas").value(3200))
                .andExpect(jsonPath("$.comprimentoNascimentoCm").value(49.5))
                .andExpect(jsonPath("$.perimetroCefalicoNascimentoCm").value(34.0))
                .andExpect(jsonPath("$.apgarUmMinuto").value(8))
                .andExpect(jsonPath("$.apgarCincoMinutos").value(9))
                .andExpect(jsonPath("$.utiNeonatal").value(false));
    }

    @Test
    void deveListarCriancasVinculadasAoResponsavelAutenticado() throws Exception {
        cadastrarUsuario("Mateus", "mateus.lista@email.com", "senha123");
        String token = obterToken("mateus.lista@email.com", "senha123");

        criarCrianca(token, "Ana Clara");
        criarCrianca(token, "Pedro Henrique");

        mockMvc.perform(get("/api/criancas")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nome").value("Ana Clara"))
                .andExpect(jsonPath("$[1].nome").value("Pedro Henrique"));
    }

    @Test
    void deveBuscarCriancaVinculadaAoResponsavelAutenticado() throws Exception {
        cadastrarUsuario("Mateus", "mateus.busca@email.com", "senha123");
        String token = obterToken("mateus.busca@email.com", "senha123");
        String id = criarCrianca(token, "Ana Clara");

        mockMvc.perform(get("/api/criancas/{id}", id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.nome").value("Ana Clara"));
    }

    @Test
    void deveAtualizarCriancaVinculadaAoResponsavelAutenticado() throws Exception {
        cadastrarUsuario("Mateus", "mateus.atualiza@email.com", "senha123");
        String token = obterToken("mateus.atualiza@email.com", "senha123");
        String id = criarCrianca(token, "Ana");

        String body = """
                {
                  "nome": "Ana Clara",
                  "dataNascimento": "2024-01-12",
                  "sexo": "FEMININO",
                  "prematura": false,
                  "semanasGestacionais": 40,
                  "diasGestacionais": 2,
                  "tipoParto": "CESAREA",
                  "pesoNascimentoGramas": 3300,
                  "comprimentoNascimentoCm": 50.5,
                  "perimetroCefalicoNascimentoCm": 34.2,
                  "apgarUmMinuto": 8,
                  "apgarCincoMinutos": 9,
                  "utiNeonatal": false,
                  "reanimacaoNeonatal": false,
                  "ictericiaNeonatal": false,
                  "dificuldadeRespiratoria": false,
                  "dificuldadeAmamentacao": false,
                  "preNatalRealizado": true,
                  "consultasPreNatal": 8,
                  "diabetesGestacional": false,
                  "hipertensaoGestacional": false,
                  "infeccaoGestacional": false,
                  "sangramentoGestacional": false,
                  "usoAlcoolGestacao": false,
                  "usoTabacoGestacao": false,
                  "outrasExposicoesGestacao": false,
                  "diasAltaHospitalar": 2,
                  "retornoHospitalarPrimeiraSemana": false,
                  "testePezinho": "REALIZADO",
                  "testeOrelhinha": "REALIZADO",
                  "testeOlhinho": "REALIZADO",
                  "testeCoracaozinho": "REALIZADO",
                  "amamentacaoPrimeiraHora": true,
                  "alimentacaoInicial": "ALEITAMENTO_MATERNO_EXCLUSIVO",
                  "observacoesNascimento": "Sem intercorrências relevantes"
                }
                """;

        mockMvc.perform(put("/api/criancas/{id}", id)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.nome").value("Ana Clara"))
                .andExpect(jsonPath("$.dataNascimento").value("2024-01-12"))
                .andExpect(jsonPath("$.semanasGestacionais").value(40))
                .andExpect(jsonPath("$.pesoNascimentoGramas").value(3300));
    }

    @Test
    void deveRemoverCriancaVinculadaAoResponsavelAutenticado() throws Exception {
        cadastrarUsuario("Mateus", "mateus.remove@email.com", "senha123");
        String token = obterToken("mateus.remove@email.com", "senha123");
        String id = criarCrianca(token, "Ana Clara");

        mockMvc.perform(delete("/api/criancas/{id}", id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/criancas/{id}", id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void naoDeveCriarCriancaSemToken() throws Exception {
        mockMvc.perform(post("/api/criancas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(criancaJson("Ana Clara", 39, 3200, false)))
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
                  "diasGestacionais": 0,
                  "tipoParto": "VAGINAL",
                  "pesoNascimentoGramas": 3200,
                  "comprimentoNascimentoCm": 49.5,
                  "perimetroCefalicoNascimentoCm": 34.0,
                  "apgarUmMinuto": 8,
                  "apgarCincoMinutos": 9,
                  "utiNeonatal": false,
                  "reanimacaoNeonatal": false,
                  "ictericiaNeonatal": false,
                  "dificuldadeRespiratoria": false,
                  "dificuldadeAmamentacao": false,
                  "preNatalRealizado": true,
                  "consultasPreNatal": 8,
                  "diabetesGestacional": false,
                  "hipertensaoGestacional": false,
                  "infeccaoGestacional": false,
                  "sangramentoGestacional": false,
                  "usoAlcoolGestacao": false,
                  "usoTabacoGestacao": false,
                  "outrasExposicoesGestacao": false,
                  "diasAltaHospitalar": 2,
                  "retornoHospitalarPrimeiraSemana": false,
                  "testePezinho": "REALIZADO",
                  "testeOrelhinha": "REALIZADO",
                  "testeOlhinho": "REALIZADO",
                  "testeCoracaozinho": "REALIZADO",
                  "amamentacaoPrimeiraHora": true,
                  "alimentacaoInicial": "ALEITAMENTO_MATERNO_EXCLUSIVO",
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
                .andExpect(jsonPath("$.erro").value("Dados inválidos"));
    }

    @Test
    void naoDeveCriarCriancaSemPesoNascimento() throws Exception {
        cadastrarUsuario("Mateus", "mateus.sempeso@email.com", "senha123");
        String token = obterToken("mateus.sempeso@email.com", "senha123");

        String body = """
                {
                  "nome": "Ana Clara",
                  "dataNascimento": "2024-01-10",
                  "sexo": "FEMININO",
                  "prematura": false,
                  "semanasGestacionais": 39,
                  "diasGestacionais": 0,
                  "tipoParto": "VAGINAL",
                  "comprimentoNascimentoCm": 49.5,
                  "perimetroCefalicoNascimentoCm": 34.0,
                  "apgarUmMinuto": 8,
                  "apgarCincoMinutos": 9,
                  "utiNeonatal": false,
                  "reanimacaoNeonatal": false,
                  "ictericiaNeonatal": false,
                  "dificuldadeRespiratoria": false,
                  "dificuldadeAmamentacao": false,
                  "preNatalRealizado": true,
                  "consultasPreNatal": 8,
                  "diabetesGestacional": false,
                  "hipertensaoGestacional": false,
                  "infeccaoGestacional": false,
                  "sangramentoGestacional": false,
                  "usoAlcoolGestacao": false,
                  "usoTabacoGestacao": false,
                  "outrasExposicoesGestacao": false,
                  "diasAltaHospitalar": 2,
                  "retornoHospitalarPrimeiraSemana": false,
                  "testePezinho": "REALIZADO",
                  "testeOrelhinha": "REALIZADO",
                  "testeOlhinho": "REALIZADO",
                  "testeCoracaozinho": "REALIZADO",
                  "amamentacaoPrimeiraHora": true,
                  "alimentacaoInicial": "ALEITAMENTO_MATERNO_EXCLUSIVO",
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
    void naoDeveCriarCriancaComNomeVazio() throws Exception {
        cadastrarUsuario("Mateus", "mateus.nomevazio@email.com", "senha123");
        String token = obterToken("mateus.nomevazio@email.com", "senha123");

        mockMvc.perform(post("/api/criancas")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(criancaJson(" ", 39, 3200, false)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("Dados inválidos"));
    }

    @Test
    void naoDeveCriarCriancaComMaisDeSeisAnosNoMvp() throws Exception {
        cadastrarUsuario("Mateus", "mateus.idademvp@email.com", "senha123");
        String token = obterToken("mateus.idademvp@email.com", "senha123");

        String body = """
                {
                  "nome": "Ana Clara",
                  "dataNascimento": "%s",
                  "sexo": "FEMININO",
                  "prematura": false,
                  "semanasGestacionais": 39,
                  "diasGestacionais": 0,
                  "tipoParto": "VAGINAL",
                  "pesoNascimentoGramas": 3200,
                  "comprimentoNascimentoCm": 49.5,
                  "perimetroCefalicoNascimentoCm": 34.0,
                  "apgarUmMinuto": 8,
                  "apgarCincoMinutos": 9,
                  "utiNeonatal": false,
                  "reanimacaoNeonatal": false,
                  "ictericiaNeonatal": false,
                  "dificuldadeRespiratoria": false,
                  "dificuldadeAmamentacao": false,
                  "preNatalRealizado": true,
                  "consultasPreNatal": 8,
                  "diabetesGestacional": false,
                  "hipertensaoGestacional": false,
                  "infeccaoGestacional": false,
                  "sangramentoGestacional": false,
                  "usoAlcoolGestacao": false,
                  "usoTabacoGestacao": false,
                  "outrasExposicoesGestacao": false,
                  "diasAltaHospitalar": 2,
                  "retornoHospitalarPrimeiraSemana": false,
                  "testePezinho": "REALIZADO",
                  "testeOrelhinha": "REALIZADO",
                  "testeOlhinho": "REALIZADO",
                  "testeCoracaozinho": "REALIZADO",
                  "amamentacaoPrimeiraHora": true,
                  "alimentacaoInicial": "ALEITAMENTO_MATERNO_EXCLUSIVO",
                  "parentesco": "PAI",
                  "aceiteConsentimento": true,
                  "versaoTermoConsentimento": "2026.07"
                }
                """.formatted(java.time.LocalDate.now().minusYears(7));

        mockMvc.perform(post("/api/criancas")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("Regra de domínio violada"))
                .andExpect(jsonPath("$.mensagens[0]").value("No momento, o Pueria acompanha crianças de até 6 anos neste cadastro."));
    }

    @Test
    void naoDeveAcessarCriancaNaoVinculadaAoResponsavelAutenticado() throws Exception {
        cadastrarUsuario("Mateus", "mateus.notfound@email.com", "senha123");
        String token = obterToken("mateus.notfound@email.com", "senha123");

        mockMvc.perform(get("/api/criancas/{id}", UUID.randomUUID())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").value("Recurso não encontrado"));
    }

    private String criarCrianca(String token, String nome) throws Exception {
        String response = mockMvc.perform(post("/api/criancas")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(criancaJson(nome, 39, 3200, false)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        int inicio = response.indexOf("\"id\":\"") + 6;
        int fim = response.indexOf("\"", inicio);
        return response.substring(inicio, fim);
    }

    private String criancaJson(String nome, int semanasGestacionais, int pesoNascimentoGramas, boolean prematura) {
        return """
                {
                  "nome": "%s",
                  "dataNascimento": "2024-01-10",
                  "sexo": "FEMININO",
                  "prematura": %s,
                  "semanasGestacionais": %d,
                  "diasGestacionais": 0,
                  "tipoParto": "VAGINAL",
                  "pesoNascimentoGramas": %d,
                  "comprimentoNascimentoCm": 49.5,
                  "perimetroCefalicoNascimentoCm": 34.0,
                  "apgarUmMinuto": 8,
                  "apgarCincoMinutos": 9,
                  "utiNeonatal": false,
                  "reanimacaoNeonatal": false,
                  "ictericiaNeonatal": false,
                  "dificuldadeRespiratoria": false,
                  "dificuldadeAmamentacao": false,
                  "preNatalRealizado": true,
                  "consultasPreNatal": 8,
                  "diabetesGestacional": false,
                  "hipertensaoGestacional": false,
                  "infeccaoGestacional": false,
                  "sangramentoGestacional": false,
                  "usoAlcoolGestacao": false,
                  "usoTabacoGestacao": false,
                  "outrasExposicoesGestacao": false,
                  "diasAltaHospitalar": 2,
                  "retornoHospitalarPrimeiraSemana": false,
                  "testePezinho": "REALIZADO",
                  "testeOrelhinha": "REALIZADO",
                  "testeOlhinho": "REALIZADO",
                  "testeCoracaozinho": "REALIZADO",
                  "amamentacaoPrimeiraHora": true,
                  "alimentacaoInicial": "ALEITAMENTO_MATERNO_EXCLUSIVO",
                  "parentesco": "PAI",
                  "aceiteConsentimento": true,
                  "versaoTermoConsentimento": "2026.07"
                }
                """.formatted(nome, prematura, semanasGestacionais, pesoNascimentoGramas);
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
