package br.com.pueria.pueria.relatorios;

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
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ResumoConsultaControllerTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private UsuarioRepositorio usuarios;
    @Autowired private CriancaRepositorio criancas;
    @Autowired private VinculoResponsavelCriancaRepositorio vinculos;

    @Test
    void deveGerarResumoBreveEDetalhadoEmPdf() throws Exception {
        String token = cadastrarEAutenticar("Mateus", "relatorio.http@email.com");
        String criancaId = criarCriancaVinculada("relatorio.http@email.com");
        mockMvc.perform(get("/api/criancas/{id}/relatorio-consulta", criancaId).header("Authorization", bearer(token)))
                .andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(header().string("Content-Disposition", containsString("resumo-consulta.pdf")))
                .andExpect(result -> assertTrue(result.getResponse().getContentAsByteArray().length > 0));
        mockMvc.perform(get("/api/criancas/{id}/relatorio-consulta", criancaId).param("detalhado", "true").header("Authorization", bearer(token)))
                .andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(header().string("Content-Disposition", containsString("resumo-consulta-detalhado.pdf")));
    }

    @Test
    void naoDeveGerarRelatorioSemVinculo() throws Exception {
        cadastrarEAutenticar("Mateus", "relatorio.a@email.com");
        String tokenB = cadastrarEAutenticar("Outro", "relatorio.b@email.com");
        String criancaId = criarCriancaVinculada("relatorio.a@email.com");
        mockMvc.perform(get("/api/criancas/{id}/relatorio-consulta", criancaId).header("Authorization", bearer(tokenB)))
                .andExpect(status().isNotFound());
    }

    private String criarCriancaVinculada(String email){var u=usuarios.buscarPorEmail(email).orElseThrow();Crianca c=Crianca.cadastrar("Ana",LocalDate.of(2024,1,10),Sexo.FEMININO,false,39,3200);criancas.salvar(c);vinculos.salvar(VinculoResponsavelCrianca.criarPrincipal(u.getId(),c.getId(),Parentesco.PAI));return c.getId().toString();}
    private String cadastrarEAutenticar(String nome,String email)throws Exception{mockMvc.perform(post("/api/auth/cadastro").contentType(MediaType.APPLICATION_JSON).content("{\"nome\":\"%s\",\"email\":\"%s\",\"senha\":\"senha123\"}".formatted(nome,email))).andExpect(status().isCreated());String r=mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content("{\"email\":\"%s\",\"senha\":\"senha123\"}".formatted(email))).andExpect(status().isOk()).andExpect(jsonPath("$.token",not(blankOrNullString()))).andReturn().getResponse().getContentAsString();String m="\"token\":\"";int i=r.indexOf(m)+m.length();return r.substring(i,r.indexOf('"',i));}
    private String bearer(String token){return "Bearer "+token;}
}
