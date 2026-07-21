package br.com.pueria.pueria.saude.infraestrutura.web;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class RegistroSaudeControllerTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private UsuarioRepositorio usuarios;
    @Autowired private CriancaRepositorio criancas;
    @Autowired private VinculoResponsavelCriancaRepositorio vinculos;

    @Test
    void deveRegistrarListarAtualizarERemoverRegistro() throws Exception {
        String token = cadastrarEAutenticar("Mateus", "saude.http@email.com");
        String criancaId = criarCriancaVinculada("saude.http@email.com");
        String resposta = mockMvc.perform(post("/api/criancas/{id}/saude/registros", criancaId)
                        .header("Authorization", bearer(token)).contentType(MediaType.APPLICATION_JSON)
                        .content(json("INTERCORRENCIA_CLINICA", "2024-02-10", "Consulta de rotina")))
                .andExpect(status().isCreated()).andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.tipo").value("INTERCORRENCIA_CLINICA"))
                .andReturn().getResponse().getContentAsString();
        String registroId = campoJson(resposta, "id");
        mockMvc.perform(get("/api/criancas/{id}/saude/registros", criancaId).header("Authorization", bearer(token)))
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)));
        mockMvc.perform(put("/api/criancas/{id}/saude/registros/{registroId}", criancaId, registroId)
                        .header("Authorization", bearer(token)).contentType(MediaType.APPLICATION_JSON)
                        .content(json("HUMOR_COMPORTAMENTO", "2024-02-11", "Humor melhor")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.descricao").value("Humor melhor"));
        mockMvc.perform(delete("/api/criancas/{id}/saude/registros/{registroId}", criancaId, registroId)
                        .header("Authorization", bearer(token))).andExpect(status().isNoContent());
    }

    @Test
    void naoDeveAceitarDataFuturaOuDescricaoAusente() throws Exception {
        String token = cadastrarEAutenticar("Mateus", "saude.invalido@email.com");
        String criancaId = criarCriancaVinculada("saude.invalido@email.com");
        mockMvc.perform(post("/api/criancas/{id}/saude/registros", criancaId).header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON).content(json("OBSERVACAO_EVENTO_MARCANTE", LocalDate.now().plusDays(1).toString(), "futuro")))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/api/criancas/{id}/saude/registros", criancaId).header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"tipo\":\"INTERCORRENCIA_CLINICA\",\"dataRegistro\":\"2024-02-10\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void naoDeveAceitarTiposInvalidos() throws Exception {
        String token = cadastrarEAutenticar("Mateus", "saude.tipo@email.com");
        String criancaId = criarCriancaVinculada("saude.tipo@email.com");
        mockMvc.perform(post("/api/criancas/{id}/saude/registros", criancaId).header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"tipo\":\"DESCONHECIDO\",\"dataRegistro\":\"2024-02-10\",\"descricao\":\"x\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void naoDeveAcessarRegistroSemVinculo() throws Exception {
        String tokenA = cadastrarEAutenticar("Mateus", "saude.a@email.com");
        String tokenB = cadastrarEAutenticar("Outro", "saude.b@email.com");
        String criancaId = criarCriancaVinculada("saude.a@email.com");
        mockMvc.perform(get("/api/criancas/{id}/saude/registros", criancaId).header("Authorization", bearer(tokenB)))
                .andExpect(status().isNotFound());
        mockMvc.perform(post("/api/criancas/{id}/saude/registros", criancaId).header("Authorization", bearer(tokenB))
                        .contentType(MediaType.APPLICATION_JSON).content(json("HUMOR_COMPORTAMENTO", "2024-02-10", "x")))
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
        mockMvc.perform(post("/api/auth/cadastro").contentType(MediaType.APPLICATION_JSON).content("{\"nome\":\"%s\",\"email\":\"%s\",\"senha\":\"senha123\"}".formatted(nome, email))).andExpect(status().isCreated());
        String resposta = mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content("{\"email\":\"%s\",\"senha\":\"senha123\"}".formatted(email))).andExpect(status().isOk()).andExpect(jsonPath("$.token", not(blankOrNullString()))).andReturn().getResponse().getContentAsString();
        return campoJson(resposta, "token");
    }
    private String json(String tipo, String data, String descricao) { return "{\"tipo\":\"%s\",\"dataRegistro\":\"%s\",\"descricao\":\"%s\"}".formatted(tipo, data, descricao); }
    private String campoJson(String conteudo, String campo) { String marcador = "\"" + campo + "\":\""; int inicio = conteudo.indexOf(marcador) + marcador.length(); return conteudo.substring(inicio, conteudo.indexOf('"', inicio)); }
    private String bearer(String token) { return "Bearer " + token; }
}
