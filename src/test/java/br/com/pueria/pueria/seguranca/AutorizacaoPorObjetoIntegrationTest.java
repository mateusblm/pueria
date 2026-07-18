package br.com.pueria.pueria.seguranca;

import br.com.pueria.pueria.responsaveis.dominio.Parentesco;
import br.com.pueria.pueria.responsaveis.dominio.VinculoResponsavelCrianca;
import br.com.pueria.pueria.responsaveis.dominio.VinculoResponsavelCriancaRepositorio;
import br.com.pueria.pueria.usuarios.dominio.UsuarioRepositorio;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AutorizacaoPorObjetoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepositorio usuarios;

    @Autowired
    private VinculoResponsavelCriancaRepositorio vinculos;

    @Test
    void responsavelNaoAcessaNemAlteraCriancaDeOutroResponsavel() throws Exception {
        String tokenResponsavelA = cadastrarEAutenticar("Responsável A", "responsavel.a.objeto@email.com");
        String tokenResponsavelB = cadastrarEAutenticar("Responsável B", "responsavel.b.objeto@email.com");
        String criancaId = criarCrianca(tokenResponsavelA, "Ana Clara");

        mockMvc.perform(get("/api/criancas/{id}", criancaId).header("Authorization", bearer(tokenResponsavelB)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").value("Recurso não encontrado"));
        mockMvc.perform(put("/api/criancas/{id}", criancaId)
                        .header("Authorization", bearer(tokenResponsavelB))
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isNotFound());
        mockMvc.perform(delete("/api/criancas/{id}", criancaId).header("Authorization", bearer(tokenResponsavelB)))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/criancas/{criancaId}/crescimento/medidas", criancaId)
                        .header("Authorization", bearer(tokenResponsavelB)))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/api/criancas/{criancaId}/desenvolvimento/marcos", criancaId)
                        .header("Authorization", bearer(tokenResponsavelB)))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/api/criancas/{criancaId}/relatorio-consulta", criancaId)
                        .header("Authorization", bearer(tokenResponsavelB)))
                .andExpect(status().isNotFound());
        mockMvc.perform(post("/api/criancas/{criancaId}/saude/registros", criancaId)
                        .header("Authorization", bearer(tokenResponsavelB))
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/criancas/{id}", criancaId).header("Authorization", bearer(tokenResponsavelA)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Ana Clara"));
    }

    @Test
    void responsavelRemovidoPerdeAcessoImediatamente() throws Exception {
        String tokenResponsavelA = cadastrarEAutenticar("Responsável A", "remocao.a.objeto@email.com");
        String tokenResponsavelB = cadastrarEAutenticar("Responsável B", "remocao.b.objeto@email.com");
        String criancaIdTexto = criarCrianca(tokenResponsavelA, "Pedro");
        UUID criancaId = UUID.fromString(criancaIdTexto);
        UUID usuarioBId = usuarios.buscarPorEmail("remocao.b.objeto@email.com").orElseThrow().getId();
        VinculoResponsavelCrianca vinculo = VinculoResponsavelCrianca.restaurar(
                UUID.randomUUID(), usuarioBId, criancaId, Parentesco.RESPONSAVEL_LEGAL, false, java.time.LocalDateTime.now()
        );
        vinculos.salvar(vinculo);

        mockMvc.perform(get("/api/criancas/{id}", criancaId).header("Authorization", bearer(tokenResponsavelB)))
                .andExpect(status().isOk());

        vinculos.removerPorId(vinculo.getId());

        mockMvc.perform(get("/api/criancas/{id}", criancaId).header("Authorization", bearer(tokenResponsavelB)))
                .andExpect(status().isNotFound());
    }

    @Test
    void identificadorConhecidoOuInexistenteProduzMesmoResultadoParaQuemNaoTemVinculo() throws Exception {
        String tokenResponsavelA = cadastrarEAutenticar("Responsável A", "enumeracao.a.objeto@email.com");
        String tokenResponsavelB = cadastrarEAutenticar("Responsável B", "enumeracao.b.objeto@email.com");
        String criancaId = criarCrianca(tokenResponsavelA, "Joana");

        mockMvc.perform(get("/api/criancas/{id}", criancaId).header("Authorization", bearer(tokenResponsavelB)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagens[0]").value("Criança não encontrada."));
        mockMvc.perform(get("/api/criancas/{id}", UUID.randomUUID()).header("Authorization", bearer(tokenResponsavelB)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagens[0]").value("Criança não encontrada."));
    }

    @Test
    void rotaDeCriancaExigeAutenticacaoAntesDaAutorizacaoPorObjeto() throws Exception {
        mockMvc.perform(get("/api/criancas/{id}", UUID.randomUUID()))
                .andExpect(status().isUnauthorized());
    }

    private String cadastrarEAutenticar(String nome, String email) throws Exception {
        String senha = "senha123";
        mockMvc.perform(post("/api/auth/cadastro").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"%s\",\"email\":\"%s\",\"senha\":\"%s\"}".formatted(nome, email, senha)))
                .andExpect(status().isCreated());

        String resposta = mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"%s\",\"senha\":\"%s\"}".formatted(email, senha)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", not(blankOrNullString())))
                .andReturn().getResponse().getContentAsString();
        return campoJson(resposta, "token");
    }

    private String criarCrianca(String token, String nome) throws Exception {
        String resposta = mockMvc.perform(post("/api/criancas").header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON).content(criancaJson(nome)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return campoJson(resposta, "id");
    }

    private String campoJson(String conteudo, String campo) {
        String marcador = "\"" + campo + "\":\"";
        int inicio = conteudo.indexOf(marcador) + marcador.length();
        return conteudo.substring(inicio, conteudo.indexOf('"', inicio));
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }

    private String criancaJson(String nome) {
        return """
                {
                  "nome": "%s", "dataNascimento": "2025-01-10", "sexo": "FEMININO", "prematura": false,
                  "semanasGestacionais": 39, "diasGestacionais": 0, "tipoParto": "VAGINAL",
                  "pesoNascimentoGramas": 3200, "comprimentoNascimentoCm": 49.5, "perimetroCefalicoNascimentoCm": 34.0,
                  "apgarUmMinuto": 8, "apgarCincoMinutos": 9, "utiNeonatal": false, "reanimacaoNeonatal": false,
                  "ictericiaNeonatal": false, "dificuldadeRespiratoria": false, "dificuldadeAmamentacao": false,
                  "preNatalRealizado": true, "consultasPreNatal": 8, "diabetesGestacional": false,
                  "hipertensaoGestacional": false, "infeccaoGestacional": false, "sangramentoGestacional": false,
                  "usoAlcoolGestacao": false, "usoTabacoGestacao": false, "outrasExposicoesGestacao": false,
                  "diasAltaHospitalar": 2, "retornoHospitalarPrimeiraSemana": false, "testePezinho": "REALIZADO",
                  "testeOrelhinha": "REALIZADO", "testeOlhinho": "REALIZADO", "testeCoracaozinho": "REALIZADO",
                  "amamentacaoPrimeiraHora": true, "alimentacaoInicial": "ALEITAMENTO_MATERNO_EXCLUSIVO",
                  "parentesco": "MAE", "aceiteConsentimento": true, "versaoTermoConsentimento": "2026.07"
                }
                """.formatted(nome);
    }
}
