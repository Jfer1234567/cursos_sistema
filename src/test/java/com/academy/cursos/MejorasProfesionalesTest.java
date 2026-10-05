package com.academy.cursos;

import com.academy.cursos.controller.admin.AdminCursoController;
import com.academy.cursos.controller.admin.AdminUsuarioController;
import com.academy.cursos.dto.CursoDTO;
import com.academy.cursos.model.AreaInvestigacion;
import com.academy.cursos.model.Curso;
import com.academy.cursos.model.enums.EstadoCurso;
import com.academy.cursos.model.Usuario;
import com.academy.cursos.model.Inscripcion;
import com.academy.cursos.model.enums.EstadoInscripcion;
import com.academy.cursos.repository.AreaInvestigacionRepository;
import com.academy.cursos.repository.InscripcionRepository;
import com.academy.cursos.repository.UsuarioRepository;
import com.academy.cursos.service.CursoService;
import com.academy.cursos.service.InscripcionService;
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

    @Autowired
    private AdminUsuarioController adminUsuarioController;

    @Autowired
    private InscripcionService inscripcionService;

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private InscripcionRepository inscripcionRepo;

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

    @Test
    void testRestriccionInscripcionDuplicadaYRegularizacion() {
        Usuario usuario = usuarioRepo.findAll().stream().findFirst().orElseThrow();
        Curso curso = cursoService.listarPublicados(null).stream().findFirst().orElseThrow();

        // 1. Regularización o nueva creación en PENDIENTE_PAGO
        Inscripcion ins = inscripcionService.iniciarInscripcion(usuario.getId(), curso.getId());
        assertNotNull(ins.getId());

        // 2. Si ya está APROBADA, debe bloquear con IllegalStateException
        ins.setEstado(EstadoInscripcion.APROBADA);
        inscripcionRepo.save(ins);

        IllegalStateException exAprobada = assertThrows(IllegalStateException.class, () -> {
            inscripcionService.iniciarInscripcion(usuario.getId(), curso.getId());
        });
        assertTrue(exAprobada.getMessage().contains("acceso aprobado"));

        // 3. Si está en PENDIENTE_VERIFICACION, debe bloquear con IllegalStateException
        ins.setEstado(EstadoInscripcion.PENDIENTE_VERIFICACION);
        inscripcionRepo.save(ins);

        IllegalStateException exVerif = assertThrows(IllegalStateException.class, () -> {
            inscripcionService.iniciarInscripcion(usuario.getId(), curso.getId());
        });
        assertTrue(exVerif.getMessage().contains("proceso de verificación"));

        // Restaurar estado a PENDIENTE_PAGO o el que tenía
        ins.setEstado(EstadoInscripcion.PENDIENTE_PAGO);
        inscripcionRepo.save(ins);
    }

    @Test
    void testPaginacionGestionCursosCuatroPorPagina() {
        // Ejecutar listarCursos con página 0
        org.springframework.ui.Model model = new org.springframework.ui.ConcurrentModel();
        String vista = adminCursoController.listarCursos(0, model);

        assertEquals("admin/cursos/lista", vista);
        assertTrue(model.containsAttribute("cursos"));
        assertTrue(model.containsAttribute("cursosPage"));
        assertTrue(model.containsAttribute("currentPage"));
        assertTrue(model.containsAttribute("totalPages"));
        assertTrue(model.containsAttribute("totalElements"));

        @SuppressWarnings("unchecked")
        java.util.List<Curso> cursos = (java.util.List<Curso>) model.getAttribute("cursos");
        assertNotNull(cursos);
        assertTrue(cursos.size() <= 4, "La primera página debe contener como máximo 4 cursos");

        int currentPage = (int) model.getAttribute("currentPage");
        assertEquals(0, currentPage);
    }

    @Test
    void testPaginacionDirectorioUsuariosCuatroPorPagina() {
        org.springframework.ui.Model model = new org.springframework.ui.ConcurrentModel();
        String vista = adminUsuarioController.listarUsuarios(null, "todos", "todos", null, 0, model);

        assertEquals("admin/usuarios/index", vista);
        assertTrue(model.containsAttribute("items"));
        assertTrue(model.containsAttribute("usuariosPage"));
        assertTrue(model.containsAttribute("cursosTabs"));
        assertTrue(model.containsAttribute("totalRegistrados"));
        assertTrue(model.containsAttribute("totalSinCursos"));
        assertTrue(model.containsAttribute("currentPage"));
        assertTrue(model.containsAttribute("totalPages"));
        assertTrue(model.containsAttribute("totalElements"));

        @SuppressWarnings("unchecked")
        java.util.List<?> items = (java.util.List<?>) model.getAttribute("items");
        assertNotNull(items);
        assertTrue(items.size() <= 4, "La primera página debe contener como máximo 4 participantes");

        int currentPage = (int) model.getAttribute("currentPage");
        assertEquals(0, currentPage);
    }

    @Test
    void testFiltradoPorMenuDesplegableDirectorioUsuarios() {
        // 1. Probar opción desplegable "sin-cursos"
        org.springframework.ui.Model modelSinCursos = new org.springframework.ui.ConcurrentModel();
        String vistaSinCursos = adminUsuarioController.listarUsuarios(null, "sin-cursos", "todos", null, 0, modelSinCursos);
        assertEquals("admin/usuarios/index", vistaSinCursos);
        assertEquals("sin-cursos", modelSinCursos.getAttribute("cursoFiltro"));
        assertNotNull(modelSinCursos.getAttribute("totalSinCursos"));

        // 2. Probar opción desplegable por ID de curso específico
        Curso curso = cursoService.listarTodos().stream().findFirst().orElseThrow();
        org.springframework.ui.Model modelCurso = new org.springframework.ui.ConcurrentModel();
        String vistaCurso = adminUsuarioController.listarUsuarios(null, String.valueOf(curso.getId()), "todos", null, 0, modelCurso);
        assertEquals("admin/usuarios/index", vistaCurso);
        assertEquals(String.valueOf(curso.getId()), modelCurso.getAttribute("cursoFiltro"));
        assertNotNull(modelCurso.getAttribute("cursoSeleccionado"));

        @SuppressWarnings("unchecked")
        java.util.List<AdminUsuarioController.CursoTabItem> cursosTabs = 
                (java.util.List<AdminUsuarioController.CursoTabItem>) modelCurso.getAttribute("cursosTabs");
        assertNotNull(cursosTabs);
        assertFalse(cursosTabs.isEmpty(), "Debe haber opciones de cursos generadas para el menú desplegable");
    }
}

