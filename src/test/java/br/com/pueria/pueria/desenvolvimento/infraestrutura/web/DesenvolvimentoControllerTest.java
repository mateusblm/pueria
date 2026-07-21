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
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class DesenvolvimentoControllerTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private UsuarioRepositorio usuarios;
    @Autowired private CriancaRepositorio criancas;
    @Autowired private VinculoResponsavelCriancaRepositorio vinculos;

    @Test
    void deveListarMarcosEResumoHome() throws Exception {
        String token = cadastrarEAutenticar("Mateus", "marcos.http@email.com");
        String criancaId = criarCriancaVinculada("marcos.http@email.com");
        mockMvc.perform(get("/api/criancas/{id}/desenvolvimento/marcos", criancaId).header("Authorization", bearer(token)))
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(0)));
        mockMvc.perform(get("/api/criancas/{id}/desenvolvimento/marcos/resumo-home", criancaId).header("Authorization", bearer(token)))
                .andExpect(status().isOk()).andExpect(jsonPath("$", is(notNullValue())));
    }

    @Test
    void naoDeveAceitarStatusInvalidoOuObservacaoExcessiva() throws Exception {
        String token = cadastrarEAutenticar("Mateus", "marcos.invalido@email.com");
        String criancaId = criarCriancaVinculada("marcos.invalido@email.com");
        String id = UUID.randomUUID().toString();
        mockMvc.perform(put("/api/criancas/{id}/desenvolvimento/marcos/{marcoId}", criancaId, id).header("Authorization", bearer(token)).contentType(MediaType.APPLICATION_JSON).content("{\"status\":\"INVALIDO\"}"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(put("/api/criancas/{id}/desenvolvimento/marcos/{marcoId}", criancaId, id).header("Authorization", bearer(token)).contentType(MediaType.APPLICATION_JSON).content("{\"observacao\":\"texto\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void naoDeveAcessarMarcosSemVinculo() throws Exception {
        cadastrarEAutenticar("Mateus", "marcos.a@email.com");
        String tokenB = cadastrarEAutenticar("Outro", "marcos.b@email.com");
        String criancaId = criarCriancaVinculada("marcos.a@email.com");
        mockMvc.perform(get("/api/criancas/{id}/desenvolvimento/marcos", criancaId).header("Authorization", bearer(tokenB)))
                .andExpect(status().isNotFound());
    }

    private String criarCriancaVinculada(String email){var u=usuarios.buscarPorEmail(email).orElseThrow();Crianca c=Crianca.cadastrar("Ana",LocalDate.of(2024,1,10),Sexo.FEMININO,false,39,3200);criancas.salvar(c);vinculos.salvar(VinculoResponsavelCrianca.criarPrincipal(u.getId(),c.getId(),Parentesco.PAI));return c.getId().toString();}
    private String cadastrarEAutenticar(String nome,String email)throws Exception{mockMvc.perform(post("/api/auth/cadastro").contentType(MediaType.APPLICATION_JSON).content("{\"nome\":\"%s\",\"email\":\"%s\",\"senha\":\"senha123\"}".formatted(nome,email))).andExpect(status().isCreated());String r=mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content("{\"email\":\"%s\",\"senha\":\"senha123\"}".formatted(email))).andExpect(status().isOk()).andExpect(jsonPath("$.token",not(blankOrNullString()))).andReturn().getResponse().getContentAsString();return campoJson(r,"token");}
    private String campoJson(String c,String campo){String m="\""+campo+"\":\"";int i=c.indexOf(m)+m.length();return c.substring(i,c.indexOf('"',i));}
    private String bearer(String token){return "Bearer "+token;}
}
