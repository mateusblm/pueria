package br.com.pueria.pueria.transitointestinal.infraestrutura.web;

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
class TransitoIntestinalControllerTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private UsuarioRepositorio usuarios;
    @Autowired private CriancaRepositorio criancas;
    @Autowired private VinculoResponsavelCriancaRepositorio vinculos;

    @Test
    void deveRegistrarListarEAtualizarTransito() throws Exception {
        String token = cadastrarEAutenticar("Mateus", "transito.http@email.com");
        String criancaId = criarCriancaVinculada("transito.http@email.com");
        String resposta = mockMvc.perform(post("/api/criancas/{id}/transito-intestinal/registros", criancaId)
                        .header("Authorization", bearer(token)).contentType(MediaType.APPLICATION_JSON)
                        .content(json("2024-02-10", 2, 8, "TIPO_4")))
                .andExpect(status().isCreated()).andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.tipoFezes").value("TIPO_4"))
                .andExpect(jsonPath("$.evacuacoesPorDia").value(2)).andReturn().getResponse().getContentAsString();
        String id = campoJson(resposta, "id");
        mockMvc.perform(get("/api/criancas/{id}/transito-intestinal/registros", criancaId).header("Authorization", bearer(token)))
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)));
        mockMvc.perform(put("/api/criancas/{id}/transito-intestinal/registros/{registroId}", criancaId, id)
                        .header("Authorization", bearer(token)).contentType(MediaType.APPLICATION_JSON)
                        .content(json("2024-02-11", 1, 6, "TIPO_5")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.tipoFezes").value("TIPO_5"));
    }

    @Test
    void deveAplicarPadraoQuandoTipoNaoInformado() throws Exception {
        String token = cadastrarEAutenticar("Mateus", "transito.padrao@email.com");
        String criancaId = criarCriancaVinculada("transito.padrao@email.com");
        mockMvc.perform(post("/api/criancas/{id}/transito-intestinal/registros", criancaId).header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"dataRegistro\":\"2024-02-10\"}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.tipoFezes").value("NAO_INFORMADO"));
    }

    @Test
    void naoDeveAceitarTiposOuNumerosInvalidos() throws Exception {
        String token = cadastrarEAutenticar("Mateus", "transito.invalido@email.com");
        String criancaId = criarCriancaVinculada("transito.invalido@email.com");
        mockMvc.perform(post("/api/criancas/{id}/transito-intestinal/registros", criancaId).header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"dataRegistro\":\"2024-02-10\",\"tipoFezes\":\"INVALIDO\",\"evacuacoesPorDia\":\"muitas\"}"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/api/criancas/{id}/transito-intestinal/registros", criancaId).header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"dataRegistro\":\"2024-02-10\",\"evacuacoesPorDia\":31}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void naoDeveAcessarTransitoSemVinculo() throws Exception {
        cadastrarEAutenticar("Mateus", "transito.a@email.com");
        String tokenB = cadastrarEAutenticar("Outro", "transito.b@email.com");
        String criancaId = criarCriancaVinculada("transito.a@email.com");
        mockMvc.perform(get("/api/criancas/{id}/transito-intestinal/registros", criancaId).header("Authorization", bearer(tokenB)))
                .andExpect(status().isNotFound());
    }

    private String criarCriancaVinculada(String email) { var u = usuarios.buscarPorEmail(email).orElseThrow(); Crianca c = Crianca.cadastrar("Ana", LocalDate.of(2024,1,10), Sexo.FEMININO, false,39,3200); criancas.salvar(c); vinculos.salvar(VinculoResponsavelCrianca.criarPrincipal(u.getId(), c.getId(), Parentesco.PAI)); return c.getId().toString(); }
    private String cadastrarEAutenticar(String nome, String email) throws Exception { mockMvc.perform(post("/api/auth/cadastro").contentType(MediaType.APPLICATION_JSON).content("{\"nome\":\"%s\",\"email\":\"%s\",\"senha\":\"senha123\"}".formatted(nome,email))).andExpect(status().isCreated()); String r=mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content("{\"email\":\"%s\",\"senha\":\"senha123\"}".formatted(email))).andExpect(status().isOk()).andExpect(jsonPath("$.token",not(blankOrNullString()))).andReturn().getResponse().getContentAsString(); return campoJson(r,"token"); }
    private String json(String data,int evacuacoes,int horas,String tipo){return "{\"dataRegistro\":\"%s\",\"tipoFezes\":\"%s\",\"evacuacoesPorDia\":%d,\"intervaloDiureseHoras\":%d,\"corUrina\":\"AMARELO_CLARO\",\"aspectoUrina\":\"SEM_ALTERACOES\",\"cheiroUrina\":\"NORMAL\"}".formatted(data,tipo,evacuacoes,horas);}
    private String campoJson(String c,String campo){String m="\""+campo+"\":\"";int i=c.indexOf(m)+m.length();return c.substring(i,c.indexOf('"',i));}
    private String bearer(String token){return "Bearer "+token;}
}
