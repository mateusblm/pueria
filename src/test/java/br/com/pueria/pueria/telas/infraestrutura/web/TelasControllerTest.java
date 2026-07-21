package br.com.pueria.pueria.telas.infraestrutura.web;

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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class TelasControllerTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private UsuarioRepositorio usuarios;
    @Autowired private CriancaRepositorio criancas;
    @Autowired private VinculoResponsavelCriancaRepositorio vinculos;

    @Test
    void deveRegistrarListarEAtualizarRegistroDeTelas() throws Exception {
        String token = cadastrarEAutenticar("Mateus", "telas.http@email.com");
        String criancaId = criarCriancaVinculada("telas.http@email.com");
        String resposta = mockMvc.perform(post("/api/criancas/{id}/telas/registros", criancaId)
                        .header("Authorization", bearer(token)).contentType(MediaType.APPLICATION_JSON)
                        .content(json("2024-02-10", 60, 120, "VIDEO_PASSIVO", "Observacao inicial")))
                .andExpect(status().isCreated()).andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.criancaId").value(criancaId))
                .andExpect(jsonPath("$.minutosMediosDia").value(77))
                .andReturn().getResponse().getContentAsString();
        String registroId = campoJson(resposta, "id");
        mockMvc.perform(get("/api/criancas/{id}/telas/registros", criancaId).header("Authorization", bearer(token)))
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)));
        mockMvc.perform(put("/api/criancas/{id}/telas/registros/{registroId}", criancaId, registroId)
                        .header("Authorization", bearer(token)).contentType(MediaType.APPLICATION_JSON)
                        .content(json("2024-02-11", 90, 180, "JOGOS", "Atualizado")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.tipoConteudoPredominante").value("JOGOS"))
                .andExpect(jsonPath("$.minutosDiaSemana").value(90));
    }

    @Test
    void deveUsarValoresPadraoQuandoCamposOpcionaisNaoInformados() throws Exception {
        String token = cadastrarEAutenticar("Mateus", "telas.padrao@email.com");
        String criancaId = criarCriancaVinculada("telas.padrao@email.com");
        mockMvc.perform(post("/api/criancas/{id}/telas/registros", criancaId).header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"dataRegistro\":\"2024-02-10\"}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.tipoConteudoPredominante").value("NAO_INFORMADO"))
                .andExpect(jsonPath("$.contextosUso", hasSize(0)));
    }

    @Test
    void naoDeveAceitarDataTipoOuMinutosInvalidos() throws Exception {
        String token = cadastrarEAutenticar("Mateus", "telas.invalido@email.com");
        String criancaId = criarCriancaVinculada("telas.invalido@email.com");
        mockMvc.perform(post("/api/criancas/{id}/telas/registros", criancaId).header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"dataRegistro\":\"amanha\",\"minutosDiaSemana\":\"muitos\"}"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/api/criancas/{id}/telas/registros", criancaId).header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"dataRegistro\":\"2024-02-10\",\"minutosDiaSemana\":1441}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void naoDeveAcessarTelasSemVinculo() throws Exception {
        cadastrarEAutenticar("Mateus", "telas.a@email.com");
        String tokenB = cadastrarEAutenticar("Outro", "telas.b@email.com");
        String criancaId = criarCriancaVinculada("telas.a@email.com");
        mockMvc.perform(get("/api/criancas/{id}/telas/registros", criancaId).header("Authorization", bearer(tokenB)))
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
    private String json(String data, int semana, int fimSemana, String tipo, String observacao) { return "{\"dataRegistro\":\"%s\",\"minutosDiaSemana\":%d,\"minutosFimSemana\":%d,\"tipoConteudoPredominante\":\"%s\",\"contextosUso\":[{\"dispositivo\":\"CELULAR\",\"conteudo\":\"%s\"}],\"observacao\":\"%s\"}".formatted(data, semana, fimSemana, tipo, tipo, observacao); }
    private String campoJson(String conteudo, String campo) { String marcador = "\"" + campo + "\":\""; int inicio = conteudo.indexOf(marcador) + marcador.length(); return conteudo.substring(inicio, conteudo.indexOf('"', inicio)); }
    private String bearer(String token) { return "Bearer " + token; }
}
