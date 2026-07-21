package br.com.pueria.pueria.alimentacao.infraestrutura.web;

import br.com.pueria.pueria.criancas.dominio.Crianca;
import br.com.pueria.pueria.criancas.dominio.CriancaRepositorio;
import br.com.pueria.pueria.criancas.dominio.Sexo;
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

import java.time.LocalDate;

import static org.hamcrest.Matchers.blankOrNullString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
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
class AlimentacaoControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UsuarioRepositorio usuarios;
    @Autowired private CriancaRepositorio criancas;
    @Autowired private VinculoResponsavelCriancaRepositorio vinculos;

    @Test
    void deveRegistrarListarEAtualizarAlimentacao() throws Exception {
        String token = cadastrarEAutenticar("Mateus", "alimentacao.http@email.com");
        String criancaId = criarCriancaVinculada("alimentacao.http@email.com");

        String resposta = mockMvc.perform(post("/api/criancas/{id}/alimentacao/registros", criancaId)
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(alimentacaoJson("ALIMENTACAO_COMPLEMENTAR_ESTABELECIDA", "2024-02-10")))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.criancaId").value(criancaId))
                .andExpect(jsonPath("$.estagioAlimentar").value("ALIMENTACAO_COMPLEMENTAR_ESTABELECIDA"))
                .andExpect(jsonPath("$.alimentosOferecidos", hasSize(1)))
                .andReturn().getResponse().getContentAsString();
        String registroId = campoJson(resposta, "id");

        mockMvc.perform(get("/api/criancas/{id}/alimentacao/registros", criancaId)
                        .header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(registroId));

        mockMvc.perform(put("/api/criancas/{id}/alimentacao/registros/{registroId}", criancaId, registroId)
                        .header("Authorization", bearer(obterToken("alimentacao.http@email.com")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(alimentacaoJson("COMIDA_DA_FAMILIA", "2024-02-11")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(registroId))
                .andExpect(jsonPath("$.estagioAlimentar").value("COMIDA_DA_FAMILIA"));
    }

    @Test
    void naoDeveRegistrarAlimentacaoSemData() throws Exception {
        String token = cadastrarEAutenticar("Mateus", "alimentacao.invalida@email.com");
        String criancaId = criarCriancaVinculada("alimentacao.invalida@email.com");

        mockMvc.perform(post("/api/criancas/{id}/alimentacao/registros", criancaId)
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estagioAlimentar\":\"APENAS_LEITE\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("Dados inválidos"));
    }

    @Test
    void naoDeveAcessarAlimentacaoDeCriancaSemVinculo() throws Exception {
        String tokenA = cadastrarEAutenticar("Mateus", "alimentacao.a@email.com");
        String tokenB = cadastrarEAutenticar("Outro", "alimentacao.b@email.com");
        String criancaId = criarCriancaVinculada("alimentacao.a@email.com");

        mockMvc.perform(get("/api/criancas/{id}/alimentacao/registros", criancaId)
                        .header("Authorization", bearer(tokenB)))
                .andExpect(status().isNotFound());

        mockMvc.perform(post("/api/criancas/{id}/alimentacao/registros", criancaId)
                        .header("Authorization", bearer(tokenB))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(alimentacaoJson("APENAS_LEITE", "2024-02-10")))
                .andExpect(status().isNotFound());
    }

    private String criarCriancaVinculada(String email) {
        var usuario = usuarios.buscarPorEmail(email).orElseThrow();
        Crianca crianca = Crianca.cadastrar("Ana", LocalDate.of(2024, 1, 10), Sexo.FEMININO, false, 39, 3200);
        criancas.salvar(crianca);
        vinculos.salvar(VinculoResponsavelCrianca.criarPrincipal(usuario.getId(), crianca.getId(), Parentesco.PAI));
        return crianca.getId().toString();
    }

    private String cadastrarEAutenticar(String nome, String email) throws Exception {
        mockMvc.perform(post("/api/auth/cadastro").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"%s\",\"email\":\"%s\",\"senha\":\"senha123\"}".formatted(nome, email)))
                .andExpect(status().isCreated());
        String resposta = mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"%s\",\"senha\":\"senha123\"}".formatted(email)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.token", not(blankOrNullString())))
                .andReturn().getResponse().getContentAsString();
        return campoJson(resposta, "token");
    }

    private String obterToken(String email) throws Exception {
        String resposta = mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"%s\",\"senha\":\"senha123\"}".formatted(email)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        return campoJson(resposta, "token");
    }

    private String alimentacaoJson(String estagio, String data) {
        return """
                {
                  "dataRegistro": "%s",
                  "tipoLeite": "MISTO",
                  "estagioAlimentar": "%s",
                  "idadeInicioAlimentacaoComplementarMeses": 6,
                  "refeicoesPorDia": 3,
                  "consomeAgua": true,
                  "usaCopo": true,
                  "texturaPredominante": "AMASSADA",
                  "consomeFrutas": true,
                  "consomeLegumesVerduras": true,
                  "tipoOrigemAlimento": "MISTO",
                  "origemPreparoAlimento": "PREPARO_EM_CASA",
                  "alimentosOferecidos": [{
                    "codigo": "banana",
                    "nome": "Banana",
                    "grupo": "FRUTA",
                    "alergenico": false,
                    "aceitacao": "BOA"
                  }]
                }
                """.formatted(data, estagio);
    }

    private String campoJson(String conteudo, String campo) {
        String marcador = "\"" + campo + "\":\"";
        int inicio = conteudo.indexOf(marcador) + marcador.length();
        return conteudo.substring(inicio, conteudo.indexOf('"', inicio));
    }

    private String bearer(String token) { return "Bearer " + token; }
}
