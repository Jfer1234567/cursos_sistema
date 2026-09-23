package com.academy.cursos;

import com.academy.cursos.controller.CatalogoCursoController;
import com.academy.cursos.controller.CertificadoController;
import com.academy.cursos.controller.PublicController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class PublicControllerTest {

    @Autowired
    private PublicController publicController;

    @Autowired
    private CatalogoCursoController catalogoCursoController;

    @Autowired
    private CertificadoController certificadoController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(
                publicController,
                catalogoCursoController,
                certificadoController
        ).build();
    }

    @Test
    void testPaginaInicio() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("public/index"))
                .andExpect(model().attributeExists("lineas"))
                .andExpect(model().attributeExists("cursosDestacados"));
    }

    @Test
    void testQuienesSomos() throws Exception {
        mockMvc.perform(get("/quienes-somos"))
                .andExpect(status().isOk())
                .andExpect(view().name("public/quienes-somos"));
    }

    @Test
    void testLineasInvestigacion() throws Exception {
        mockMvc.perform(get("/lineas-investigacion"))
                .andExpect(status().isOk())
                .andExpect(view().name("public/lineas-investigacion"))
                .andExpect(model().attributeExists("lineas"));
    }

    @Test
    void testCatalogo() throws Exception {
        mockMvc.perform(get("/catalogo"))
                .andExpect(status().isOk())
                .andExpect(view().name("public/catalogo"))
                .andExpect(model().attributeExists("cursos"));
    }

    @Test
    void testVerificarCertificadoPagina() throws Exception {
        mockMvc.perform(get("/verificar"))
                .andExpect(status().isOk())
                .andExpect(view().name("verificar/certificado"));
    }
}
