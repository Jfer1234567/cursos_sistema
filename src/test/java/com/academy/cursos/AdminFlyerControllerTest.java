package com.academy.cursos;

import com.academy.cursos.controller.admin.AdminFlyerController;
import com.academy.cursos.model.Curso;
import com.academy.cursos.service.CursoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class AdminFlyerControllerTest {

    @Autowired
    private AdminFlyerController adminFlyerController;

    @Autowired
    private CursoService cursoService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(adminFlyerController).build();
    }

    @Test
    void testDisenoFlyerGeneral() throws Exception {
        mockMvc.perform(get("/admin/flyer"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/flyer/editor"))
                .andExpect(model().attributeExists("cursos"))
                .andExpect(model().attributeExists("configPago"));
    }

    @Test
    void testDisenoFlyerPorCurso() throws Exception {
        Curso primerCurso = cursoService.listarTodos().stream().findFirst().orElseThrow();

        Curso cursoModel = (Curso) mockMvc.perform(get("/admin/flyer/curso/" + primerCurso.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/flyer/editor"))
                .andExpect(model().attributeExists("cursos"))
                .andExpect(model().attributeExists("cursoSeleccionado"))
                .andReturn()
                .getModelAndView()
                .getModel()
                .get("cursoSeleccionado");

        assertNotNull(cursoModel);
        assertEquals(primerCurso.getId(), cursoModel.getId());
    }

    @Test
    void testRedirigirFlyerCurso() throws Exception {
        mockMvc.perform(get("/admin/cursos/1/flyer"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/flyer/curso/1"));
    }
}
