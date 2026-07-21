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
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class RelatoDesenvolvimentoControllerTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private UsuarioRepositorio usuarios;
    @Autowired private CriancaRepositorio criancas;
    @Autowired private VinculoResponsavelCriancaRepositorio vinculos;

    @Test
    void deveRegistrarEListarRelato() throws Exception {
        String token = cadastrarEAutenticar("Mateus", "relato.http@email.com");
        String criancaId = criarCriancaVinculada("relato.http@email.com");
        mockMvc.perform(post("/api/criancas/{id}/desenvolvimento/relatos", criancaId)
                        .header("Authorization", bearer(token)).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tipo\":\"PREOCUPACAO_FAMILIA\",\"descricao\":\"Observacao de acompanhamento\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.tipo").value("PREOCUPACAO_FAMILIA"));
        mockMvc.perform(get("/api/criancas/{id}/desenvolvimento/relatos", criancaId).header("Authorization", bearer(token)))
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].descricao").value("Observacao de acompanhamento"));
    }

    @Test
    void naoDeveAceitarTipoOuDescricaoInvalidos() throws Exception {
        String token = cadastrarEAutenticar("Mateus", "relato.invalido@email.com");
        String criancaId = criarCriancaVinculada("relato.invalido@email.com");
        mockMvc.perform(post("/api/criancas/{id}/desenvolvimento/relatos", criancaId).header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"tipo\":\"DESCONHECIDO\",\"descricao\":\"x\"}"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/api/criancas/{id}/desenvolvimento/relatos", criancaId).header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"tipo\":\"PERDA_HABILIDADE\",\"descricao\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void naoDeveListarRelatoSemVinculo() throws Exception {
        cadastrarEAutenticar("Mateus", "relato.a@email.com");
        String tokenB = cadastrarEAutenticar("Outro", "relato.b@email.com");
        String criancaId = criarCriancaVinculada("relato.a@email.com");
        mockMvc.perform(get("/api/criancas/{id}/desenvolvimento/relatos", criancaId).header("Authorization", bearer(tokenB)))
                .andExpect(status().isNotFound());
    }

    private String criarCriancaVinculada(String email) { var u=usuarios.buscarPorEmail(email).orElseThrow(); Crianca c=Crianca.cadastrar("Ana",LocalDate.of(2024,1,10),Sexo.FEMININO,false,39,3200); criancas.salvar(c); vinculos.salvar(VinculoResponsavelCrianca.criarPrincipal(u.getId(),c.getId(),Parentesco.PAI)); return c.getId().toString(); }
    private String cadastrarEAutenticar(String nome,String email)throws Exception { mockMvc.perform(post("/api/auth/cadastro").contentType(MediaType.APPLICATION_JSON).content("{\"nome\":\"%s\",\"email\":\"%s\",\"senha\":\"senha123\"}".formatted(nome,email))).andExpect(status().isCreated()); String r=mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content("{\"email\":\"%s\",\"senha\":\"senha123\"}".formatted(email))).andExpect(status().isOk()).andExpect(jsonPath("$.token",not(blankOrNullString()))).andReturn().getResponse().getContentAsString(); String m="\"token\":\"";int i=r.indexOf(m)+m.length();return r.substring(i,r.indexOf('"',i)); }
    private String bearer(String token){return "Bearer "+token;}
}
