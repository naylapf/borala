package com.corp.borala;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Confere as regras de acesso: o que é público, o que exige login e o que cada papel pode abrir. */
@SpringBootTest
@AutoConfigureMockMvc
class AcessoTest {

    @Autowired
    MockMvc mvc;

    @Test
    void paginasPublicasAbremSemLogin() throws Exception {
        mvc.perform(get("/")).andExpect(status().isOk());
        mvc.perform(get("/login")).andExpect(status().isOk());
        mvc.perform(get("/cadastro")).andExpect(status().isOk());
    }

    @Test
    void paginasTemEstruturaSemanticaParaLeitoresDeTela() throws Exception {
        for (String caminho : new String[]{"/", "/login", "/cadastro", "/cadastro/cliente", "/cadastro/dono"}) {
            mvc.perform(get(caminho))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("<main id=\"conteudo\"")))
                    .andExpect(content().string(containsString("<footer")))
                    .andExpect(content().string(containsString("lang=\"pt-BR\"")));
        }
        mvc.perform(get("/"))
                .andExpect(content().string(containsString("<header>")))
                .andExpect(content().string(containsString("aria-label=\"Menu principal\"")))
                .andExpect(content().string(containsString("Pular para o conteúdo")));
    }

    @Test
    void areasRestritasMandamParaOLogin() throws Exception {
        for (String caminho : new String[]{"/meu/favoritos", "/restaurantes", "/usuarios", "/dono"}) {
            mvc.perform(get(caminho)).andExpect(redirectedUrl("/login"));
        }
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    void clienteNaoAcessaAreaDeAdminNemDeDono() throws Exception {
        mvc.perform(get("/usuarios")).andExpect(status().isForbidden());
        mvc.perform(get("/restaurantes")).andExpect(status().isForbidden());
        mvc.perform(get("/dono")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "DONO")
    void donoNaoAcessaAreaDoCliente() throws Exception {
        mvc.perform(get("/meu/favoritos")).andExpect(status().isForbidden());
        mvc.perform(get("/usuarios")).andExpect(status().isForbidden());
    }
}
