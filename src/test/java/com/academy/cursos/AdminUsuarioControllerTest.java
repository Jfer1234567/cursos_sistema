package com.academy.cursos;

import com.academy.cursos.controller.admin.AdminDashboardController;
import com.academy.cursos.controller.admin.AdminUsuarioController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class AdminUsuarioControllerTest {

    @Autowired
    private AdminUsuarioController adminUsuarioController;

    @Autowired
    private AdminDashboardController adminDashboardController;

    private MockMvc mockMvcUsuarios;
    private MockMvc mockMvcDashboard;

    @BeforeEach
    void setUp() {
        this.mockMvcUsuarios = MockMvcBuilders.standaloneSetup(adminUsuarioController).build();
        this.mockMvcDashboard = MockMvcBuilders.standaloneSetup(adminDashboardController).build();
    }

    @Test
    void testDashboardMuestraMetricaUsuarios() throws Exception {
        mockMvcDashboard.perform(get("/admin"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/dashboard"))
                .andExpect(model().attributeExists("totalUsuarios"))
                .andExpect(model().attributeExists("cursosActivos"))
                .andExpect(model().attributeExists("pagosPendientes"));
    }

    @Test
    void testListarUsuariosRegistrados() throws Exception {
        mockMvcUsuarios.perform(get("/admin/usuarios"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/usuarios/index"))
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attributeExists("totalRegistrados"));
    }

    @Test
    void testFiltrarUsuariosRegistrados() throws Exception {
        mockMvcUsuarios.perform(get("/admin/usuarios").param("filtro", "Juan"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/usuarios/index"))
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attribute("filtro", "Juan"));
    }
}
