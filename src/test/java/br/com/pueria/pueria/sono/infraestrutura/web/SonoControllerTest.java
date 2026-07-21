package br.com.pueria.pueria.sono.infraestrutura.web;

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
class SonoControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UsuarioRepositorio usuarios;
    @Autowired private CriancaRepositorio criancas;
    @Autowired private VinculoResponsavelCriancaRepositorio vinculos;

    @Test
    void deveRegistrarListarEAtualizarSono() throws Exception {
        String token = cadastrarEAutenticar("Mateus", "sono.http@email.com");
        String criancaId = criarCriancaVinculada("sono.http@email.com");

        String resposta = mockMvc.perform(post("/api/criancas/{id}/sono/registros", criancaId)
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(sonoJson("20:00", "10:00", 180)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.criancaId").value(criancaId))
                .andExpect(jsonPath("$.minutosSonoNoturno").value(840))
                .andExpect(jsonPath("$.analise.classificacaoDuracao").value("FAIXA_ESPERADA"))
                .andReturn().getResponse().getContentAsString();
        String registroId = campoJson(resposta, "id");

        mockMvc.perform(get("/api/criancas/{id}/sono/registros", criancaId)
                        .header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(registroId));

        mockMvc.perform(put("/api/criancas/{id}/sono/registros/{registroId}", criancaId, registroId)
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(sonoJson("21:00", "06:30", 120)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(registroId))
                .andExpect(jsonPath("$.horarioDormiu").value("21:00:00"))
                .andExpect(jsonPath("$.minutosSonoNoturno").value(570));
    }

    @Test
    void naoDeveRegistrarSonoComQuantidadeDeCochilosForaDoLimite() throws Exception {
        String token = cadastrarEAutenticar("Mateus", "sono.invalido@email.com");
        String criancaId = criarCriancaVinculada("sono.invalido@email.com");

        mockMvc.perform(post("/api/criancas/{id}/sono/registros", criancaId)
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(sonoJson("20:00", "06:00", 180).replace("\"quantidadeCochilos\": 2", "\"quantidadeCochilos\": 13")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("Dados inválidos"));
    }

    @Test
    void naoDeveAcessarSonoDeCriancaSemVinculo() throws Exception {
        String tokenA = cadastrarEAutenticar("Mateus", "sono.a@email.com");
        String tokenB = cadastrarEAutenticar("Outro", "sono.b@email.com");
        String criancaId = criarCriancaVinculada("sono.a@email.com");

        mockMvc.perform(get("/api/criancas/{id}/sono/registros", criancaId)
                        .header("Authorization", bearer(tokenB)))
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

    private String sonoJson(String dormiu, String acordou, int minutosCochilos) {
        return """
                {
                  "dataRegistro": "2024-02-10",
                  "horarioDormiu": "%s",
                  "horarioAcordou": "%s",
                  "quantidadeCochilos": 2,
                  "minutosCochilos": %d,
                  "despertaresNoturnos": 1,
                  "dificuldadeIniciarSono": false,
                  "rotinaSonoConsistente": true,
                  "telasAntesDormir": false,
                  "superficieSono": "BERCO",
                  "ambienteSono": "QUARTO_DOS_RESPONSAVEIS",
                  "tiposDespertarNoturno": ["VOLTA_A_DORMIR_RAPIDO"],
                  "roncosFrequentes": false,
                  "pausasRespiratoriasPercebidas": false,
                  "sonoAgitado": false,
                  "acordaBemDisposto": true,
                  "preocupacaoFamilia": false
                }
                """.formatted(dormiu, acordou, minutosCochilos);
    }

    private String campoJson(String conteudo, String campo) {
        String marcador = "\"" + campo + "\":\"";
        int inicio = conteudo.indexOf(marcador) + marcador.length();
        return conteudo.substring(inicio, conteudo.indexOf('"', inicio));
    }

    private String bearer(String token) { return "Bearer " + token; }
}
