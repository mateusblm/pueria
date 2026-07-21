package br.com.pueria.pueria.crescimento.infraestrutura.web;

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
class CrescimentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepositorio usuarios;

    @Autowired
    private CriancaRepositorio criancas;

    @Autowired
    private VinculoResponsavelCriancaRepositorio vinculos;

    @Test
    void deveRegistrarListarECalcularCurvasDeUmaMedida() throws Exception {
        String token = cadastrarEAutenticar("Mateus", "crescimento.lista@email.com");
        String criancaId = criarCriancaVinculada("crescimento.lista@email.com");

        String resposta = mockMvc.perform(post("/api/criancas/{criancaId}/crescimento/medidas", criancaId)
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(medidaJson("2024-02-10", "4.20", "55.0", "38.0", "CONSULTA")))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.criancaId").value(criancaId))
                .andExpect(jsonPath("$.pesoKg").value(4.20))
                .andExpect(jsonPath("$.origem").value("CONSULTA"))
                .andReturn().getResponse().getContentAsString();

        String medidaId = campoJson(resposta, "id");

        mockMvc.perform(get("/api/criancas/{criancaId}/crescimento/medidas", criancaId)
                        .header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(medidaId));

        mockMvc.perform(get("/api/criancas/{criancaId}/crescimento/medidas/curvas", criancaId)
                        .header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].medidaId").value(medidaId))
                .andExpect(jsonPath("$[0].resultados", hasSize(5)));
    }

    @Test
    void deveAtualizarERemoverMedidaDaCriancaVinculada() throws Exception {
        String token = cadastrarEAutenticar("Mateus", "crescimento.edicao@email.com");
        String criancaId = criarCriancaVinculada("crescimento.edicao@email.com");
        String resposta = mockMvc.perform(post("/api/criancas/{criancaId}/crescimento/medidas", criancaId)
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(medidaJson("2024-02-10", "4.20", "55.0", "38.0", "CASA")))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        String medidaId = campoJson(resposta, "id");

        mockMvc.perform(put("/api/criancas/{criancaId}/crescimento/medidas/{medidaId}", criancaId, medidaId)
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(medidaJson("2024-02-11", "4.50", "56.0", "38.5", "CONSULTA")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(medidaId))
                .andExpect(jsonPath("$.pesoKg").value(4.50))
                .andExpect(jsonPath("$.origem").value("CONSULTA"));

        mockMvc.perform(delete("/api/criancas/{criancaId}/crescimento/medidas/{medidaId}", criancaId, medidaId)
                        .header("Authorization", bearer(token)))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/criancas/{criancaId}/crescimento/medidas", criancaId)
                        .header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void naoDeveAceitarMedidaComDataFuturaOuValorForaDoLimite() throws Exception {
        String token = cadastrarEAutenticar("Mateus", "crescimento.invalido@email.com");
        String criancaId = criarCriancaVinculada("crescimento.invalido@email.com");

        mockMvc.perform(post("/api/criancas/{criancaId}/crescimento/medidas", criancaId)
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(medidaJson(LocalDate.now().plusDays(1).toString(), "0.1", "55.0", "38.0", "CASA")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("Dados inválidos"))
                .andExpect(jsonPath("$.mensagens").isArray());
    }

    @Test
    void naoDeveAceitarTextoEmDataOuCampoNumerico() throws Exception {
        String token = cadastrarEAutenticar("Mateus", "crescimento.tipo-invalido@email.com");
        String criancaId = criarCriancaVinculada("crescimento.tipo-invalido@email.com");

        mockMvc.perform(post("/api/criancas/{criancaId}/crescimento/medidas", criancaId)
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"dataMedicao\":\"ontem\",\"pesoKg\":\"muitos\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void naoDeveAcessarMedidasDeCriancaSemVinculo() throws Exception {
        String tokenA = cadastrarEAutenticar("Mateus", "crescimento.a@email.com");
        String tokenB = cadastrarEAutenticar("Outro", "crescimento.b@email.com");
        String criancaId = criarCriancaVinculada("crescimento.a@email.com");

        mockMvc.perform(get("/api/criancas/{criancaId}/crescimento/medidas", criancaId)
                        .header("Authorization", bearer(tokenB)))
                .andExpect(status().isNotFound());

        mockMvc.perform(post("/api/criancas/{criancaId}/crescimento/medidas", criancaId)
                        .header("Authorization", bearer(tokenB))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(medidaJson("2024-02-10", "4.20", "55.0", "38.0", "CASA")))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/criancas/{criancaId}/crescimento/medidas", criancaId)
                        .header("Authorization", bearer(tokenA)))
                .andExpect(status().isOk());
    }

    private String criarCriancaVinculada(String email) {
        var usuario = usuarios.buscarPorEmail(email).orElseThrow();
        Crianca crianca = Crianca.cadastrar("Ana", LocalDate.of(2024, 1, 10), Sexo.FEMININO, false, 39, 3200);
        criancas.salvar(crianca);
        vinculos.salvar(VinculoResponsavelCrianca.criarPrincipal(usuario.getId(), crianca.getId(), Parentesco.PAI));
        return crianca.getId().toString();
    }

    private String cadastrarEAutenticar(String nome, String email) throws Exception {
        mockMvc.perform(post("/api/auth/cadastro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"%s\",\"email\":\"%s\",\"senha\":\"senha123\"}".formatted(nome, email)))
                .andExpect(status().isCreated());

        String resposta = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"%s\",\"senha\":\"senha123\"}".formatted(email)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", not(blankOrNullString())))
                .andReturn().getResponse().getContentAsString();
        return campoJson(resposta, "token");
    }

    private String medidaJson(String data, String peso, String comprimento, String perimetro, String origem) {
        return """
                {
                  "dataMedicao": "%s",
                  "pesoKg": %s,
                  "comprimentoCm": %s,
                  "perimetroCefalicoCm": %s,
                  "origem": "%s",
                  "responsavelMedicao": "FAMILIA",
                  "observacao": "Medição de acompanhamento"
                }
                """.formatted(data, peso, comprimento, perimetro, origem);
    }

    private String campoJson(String conteudo, String campo) {
        String marcador = "\"" + campo + "\":\"";
        int inicio = conteudo.indexOf(marcador) + marcador.length();
        return conteudo.substring(inicio, conteudo.indexOf('"', inicio));
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }
}
