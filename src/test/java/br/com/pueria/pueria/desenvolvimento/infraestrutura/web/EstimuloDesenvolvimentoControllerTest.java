package br.com.pueria.pueria.desenvolvimento.infraestrutura.web;

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
import java.util.Collection;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class EstimuloDesenvolvimentoControllerTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private UsuarioRepositorio usuarios;
    @Autowired private CriancaRepositorio criancas;
    @Autowired private VinculoResponsavelCriancaRepositorio vinculos;

    @Test
    void deveListarEstimulosRecomendacoesEHistorico() throws Exception {
        String token = cadastrarEAutenticar("Mateus", "estimulo.http@email.com");
        String criancaId = criarCriancaVinculada("estimulo.http@email.com");
        mockMvc.perform(get("/api/criancas/{id}/desenvolvimento/estimulos", criancaId).header("Authorization", bearer(token)))
                .andExpect(status().isOk()).andExpect(jsonPath("$", isA(Collection.class)));
        mockMvc.perform(get("/api/criancas/{id}/desenvolvimento/estimulos/recomendacoes", criancaId).param("idadeMeses", "12").header("Authorization", bearer(token)))
                .andExpect(status().isOk()).andExpect(jsonPath("$", isA(Collection.class)));
        mockMvc.perform(get("/api/criancas/{id}/desenvolvimento/estimulos/historico", criancaId).header("Authorization", bearer(token)))
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void naoDeveAceitarObservacaoExcessivaNemEstimuloInexistente() throws Exception {
        String token = cadastrarEAutenticar("Mateus", "estimulo.invalido@email.com");
        String criancaId = criarCriancaVinculada("estimulo.invalido@email.com");
        String observacao = "x".repeat(501);
        mockMvc.perform(put("/api/criancas/{id}/desenvolvimento/estimulos/{estimuloId}", criancaId, UUID.randomUUID())
                        .header("Authorization", bearer(token)).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"observacao\":\"" + observacao + "\"}"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(put("/api/criancas/{id}/desenvolvimento/estimulos/{estimuloId}", criancaId, UUID.randomUUID())
                        .header("Authorization", bearer(token)).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"observacao\":\"teste\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void naoDeveAcessarEstimulosSemVinculo() throws Exception {
        cadastrarEAutenticar("Mateus", "estimulo.a@email.com");
        String tokenB = cadastrarEAutenticar("Outro", "estimulo.b@email.com");
        String criancaId = criarCriancaVinculada("estimulo.a@email.com");
        mockMvc.perform(get("/api/criancas/{id}/desenvolvimento/estimulos", criancaId).header("Authorization", bearer(tokenB)))
                .andExpect(status().isNotFound());
    }

    private String criarCriancaVinculada(String email){var u=usuarios.buscarPorEmail(email).orElseThrow();Crianca c=Crianca.cadastrar("Ana",LocalDate.of(2024,1,10),Sexo.FEMININO,false,39,3200);criancas.salvar(c);vinculos.salvar(VinculoResponsavelCrianca.criarPrincipal(u.getId(),c.getId(),Parentesco.PAI));return c.getId().toString();}
    private String cadastrarEAutenticar(String nome,String email)throws Exception{mockMvc.perform(post("/api/auth/cadastro").contentType(MediaType.APPLICATION_JSON).content("{\"nome\":\"%s\",\"email\":\"%s\",\"senha\":\"senha123\"}".formatted(nome,email))).andExpect(status().isCreated());String r=mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content("{\"email\":\"%s\",\"senha\":\"senha123\"}".formatted(email))).andExpect(status().isOk()).andExpect(jsonPath("$.token",not(blankOrNullString()))).andReturn().getResponse().getContentAsString();String m="\"token\":\"";int i=r.indexOf(m)+m.length();return r.substring(i,r.indexOf('"',i));}
    private String bearer(String token){return "Bearer "+token;}
}
