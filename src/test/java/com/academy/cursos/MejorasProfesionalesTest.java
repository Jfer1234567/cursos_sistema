package com.academy.cursos;

import com.academy.cursos.controller.admin.AdminCursoController;
import com.academy.cursos.dto.CursoDTO;
import com.academy.cursos.model.AreaInvestigacion;
import com.academy.cursos.model.Curso;
import com.academy.cursos.model.enums.EstadoCurso;
import com.academy.cursos.repository.AreaInvestigacionRepository;
import com.academy.cursos.service.CursoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class MejorasProfesionalesTest {

    @Autowired
    private CursoService cursoService;

    @Autowired
    private AreaInvestigacionRepository areaRepo;

    @Autowired
    private AdminCursoController adminCursoController;

    @Test
    void testGuardarCursoConMejorasProfesionales() {
        AreaInvestigacion area = areaRepo.findAll().stream().findFirst().orElseThrow();

        CursoDTO dto = new CursoDTO();
        dto.setNombre("Curso Test Inteligencia Computacional Avanzada");
        dto.setDescripcionCorta("Resumen de prueba");
        dto.setDescripcion("Contenido extenso y detallado de prueba con temario completo");
        dto.setAreaInvestigacionId(area.getId());
        dto.setDocenteResponsable("Dr. Leonid Alemán");
        dto.setDuracion("45 horas académicas");
        dto.setPrecio(BigDecimal.valueOf(160.00));
        dto.setPrecioComunidad(BigDecimal.valueOf(110.00));
        dto.setCuposTotales(25);
        dto.setCreditos(3);
        dto.setEnlaceClase("https://meet.google.com/test-clase");
        dto.setEnlaceWhatsapp("https://chat.whatsapp.com/test-grupo");
        dto.setEstado(EstadoCurso.PUBLICADO);

        Curso guardado = cursoService.guardarOActualizar(dto);

        assertNotNull(guardado.getId());
        assertEquals("https://chat.whatsapp.com/test-grupo", guardado.getEnlaceWhatsapp());
        assertEquals(BigDecimal.valueOf(110.00), guardado.getPrecioComunidad());
        assertEquals(3, guardado.getCreditos());
    }

    @Test
    void testExportacionPadronCsvConBomUtf8() throws Exception {
        Curso curso = cursoService.listarTodos().stream().findFirst().orElseThrow();

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(adminCursoController).build();

        MockHttpServletResponse response = mockMvc.perform(get("/admin/cursos/" + curso.getId() + "/exportar-csv"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        byte[] content = response.getContentAsByteArray();
        assertTrue(content.length >= 3, "El archivo debe contener bytes");

        // Verify UTF-8 BOM (0xEF, 0xBB, 0xBF)
        assertEquals((byte) 0xEF, content[0]);
        assertEquals((byte) 0xBB, content[1]);
        assertEquals((byte) 0xBF, content[2]);

        String csvString = new String(content, StandardCharsets.UTF_8);
        assertTrue(csvString.contains("N°;Apellidos y Nombres;Correo;Teléfono;Tipo Doc;N° Documento;Institución;Estado Inscripción;Monto Pagado;Código Operación;Fecha Inscripción"));
        assertTrue(response.getHeader("Content-Disposition").contains(".csv"));
    }
}
