package com.academy.cursos;

import com.academy.cursos.controller.PublicController;
import com.academy.cursos.controller.admin.AdminDashboardController;
import com.academy.cursos.controller.admin.AdminMensajeController;
import com.academy.cursos.dto.ContactoDTO;
import com.academy.cursos.model.MensajeContacto;
import com.academy.cursos.repository.MensajeContactoRepository;
import com.academy.cursos.service.MensajeContactoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
public class MensajeContactoTest {

    @Autowired
    private PublicController publicController;

    @Autowired
    private AdminMensajeController adminMensajeController;

    @Autowired
    private AdminDashboardController adminDashboardController;

    @Autowired
    private MensajeContactoService mensajeService;

    @Autowired
    private MensajeContactoRepository mensajeRepo;

    private MockMvc mockMvcPublic;
    private MockMvc mockMvcAdmin;
    private MockMvc mockMvcDashboard;

    @BeforeEach
    void setUp() {
        this.mockMvcPublic = MockMvcBuilders.standaloneSetup(publicController).build();
        this.mockMvcAdmin = MockMvcBuilders.standaloneSetup(adminMensajeController).build();
        this.mockMvcDashboard = MockMvcBuilders.standaloneSetup(adminDashboardController).build();
    }

    @Test
    void testContactoGetMuestraFormulario() throws Exception {
        mockMvcPublic.perform(get("/contacto"))
                .andExpect(status().isOk())
                .andExpect(view().name("public/contacto"))
                .andExpect(model().attributeExists("contactoDTO"));
    }

    @Test
    void testContactoPostConDatosValidos() throws Exception {
        mockMvcPublic.perform(post("/contacto")
                        .param("nombreCompleto", "Carlos Alumno Test")
                        .param("correo", "carlos.test@unap.edu.pe")
                        .param("telefono", "951234567")
                        .param("asunto", "Consulta sobre certificado")
                        .param("mensaje", "Deseo consultar sobre la emisión del certificado oficial del curso de IA."))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/contacto"))
                .andExpect(flash().attributeExists("successMsg"));

        List<MensajeContacto> encontrados = mensajeRepo.buscarMensajes("Carlos Alumno Test");
        assertFalse(encontrados.isEmpty());
        MensajeContacto m = encontrados.get(0);
        assertEquals("carlos.test@unap.edu.pe", m.getCorreo());
        assertEquals("Consulta sobre certificado", m.getAsunto());
        assertFalse(m.getLeido());
        assertFalse(m.getRespondido());
    }

    @Test
    void testContactoPostConDatosInvalidos() throws Exception {
        mockMvcPublic.perform(post("/contacto")
                        .param("nombreCompleto", "")
                        .param("correo", "correo-invalido")
                        .param("asunto", "")
                        .param("mensaje", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("public/contacto"))
                .andExpect(model().hasErrors());
    }

    @Test
    void testAdminMensajesBandejaYFiltros() throws Exception {
        ContactoDTO dto = new ContactoDTO();
        dto.setNombreCompleto("Maria Becerra");
        dto.setCorreo("maria.becerra@gmail.com");
        dto.setTelefono("987654321");
        dto.setAsunto("Inscripción curso Python");
        dto.setMensaje("¿Hay vacantes disponibles para el curso?");
        mensajeService.guardarMensaje(dto);

        mockMvcAdmin.perform(get("/admin/mensajes"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/mensajes/lista"))
                .andExpect(model().attributeExists("mensajes"))
                .andExpect(model().attributeExists("noLeidos"));

        mockMvcAdmin.perform(get("/admin/mensajes").param("filtro", "Becerra"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/mensajes/lista"))
                .andExpect(model().attributeExists("mensajes"))
                .andExpect(model().attribute("filtro", "Becerra"));
    }

    @Test
    void testAdminMensajeDetalleYTransicionEstados() throws Exception {
        ContactoDTO dto = new ContactoDTO();
        dto.setNombreCompleto("Pedro Administrador");
        dto.setCorreo("pedro@gmail.com");
        dto.setAsunto("Consulta de horario");
        dto.setMensaje("¿Qué días se dicta la clase en vivo?");
        MensajeContacto creado = mensajeService.guardarMensaje(dto);

        assertFalse(creado.getLeido());

        // Ver detalle auto-marca como leído
        mockMvcAdmin.perform(get("/admin/mensajes/" + creado.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/mensajes/detalle"))
                .andExpect(model().attributeExists("mensaje"));

        MensajeContacto leido = mensajeRepo.findById(creado.getId()).orElseThrow();
        assertTrue(leido.getLeido());
        assertFalse(leido.getRespondido());

        // Marcar como respondido
        mockMvcAdmin.perform(post("/admin/mensajes/" + creado.getId() + "/marcar-respondido"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/mensajes/" + creado.getId()))
                .andExpect(flash().attributeExists("successMsg"));

        MensajeContacto respondido = mensajeRepo.findById(creado.getId()).orElseThrow();
        assertTrue(respondido.getRespondido());
    }

    @Test
    void testDashboardMuestraMetricaMensajesNoLeidos() throws Exception {
        mockMvcDashboard.perform(get("/admin"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/dashboard"))
                .andExpect(model().attributeExists("mensajesNoLeidos"));
    }
}
