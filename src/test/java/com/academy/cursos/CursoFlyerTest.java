package com.academy.cursos;

import com.academy.cursos.dto.CursoDTO;
import com.academy.cursos.model.AreaInvestigacion;
import com.academy.cursos.model.Curso;
import com.academy.cursos.model.enums.EstadoCurso;
import com.academy.cursos.repository.AreaInvestigacionRepository;
import com.academy.cursos.repository.CursoRepository;
import com.academy.cursos.service.CursoService;
import com.academy.cursos.model.Inscripcion;
import com.academy.cursos.model.Usuario;
import com.academy.cursos.model.enums.EstadoInscripcion;
import com.academy.cursos.model.enums.Rol;
import com.academy.cursos.model.enums.TipoDocumento;
import com.academy.cursos.repository.InscripcionRepository;
import com.academy.cursos.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class CursoFlyerTest {

    @Autowired
    private CursoService cursoService;

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private AreaInvestigacionRepository areaRepository;

    @Autowired
    private InscripcionRepository inscripcionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    @DisplayName("Debe registrar y consultar un flyer publicado en un curso")
    void testGuardarYPublicarFlyer() {
        AreaInvestigacion area = areaRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("No hay áreas en la BD de prueba"));

        CursoDTO dto = new CursoDTO();
        dto.setNombre("Curso Especializado con Flyer");
        dto.setDescripcion("Descripción detallada del temario de prueba");
        dto.setDescripcionCorta("Breve descripción");
        dto.setAreaInvestigacionId(area.getId());
        dto.setDocenteResponsable("Dr. Ponente de Prueba");
        dto.setPrecio(new BigDecimal("150.00"));
        dto.setPrecioComunidad(new BigDecimal("90.00"));
        dto.setCuposTotales(30);
        dto.setDuracion("40 horas");
        dto.setCreditos(3);
        dto.setEstado(EstadoCurso.PUBLICADO);
        dto.setFlyerUrl("https://res.cloudinary.com/fq7lbg0c/image/upload/v12345/flyer_test.jpg");
        dto.setFlyerPublicado(true);

        Curso guardado = cursoService.guardarOActualizar(dto);

        assertNotNull(guardado.getId());
        assertEquals("https://res.cloudinary.com/fq7lbg0c/image/upload/v12345/flyer_test.jpg", guardado.getFlyerUrl());
        assertTrue(guardado.getFlyerPublicado());

        // Consultar el flyer principal para la página de inicio
        Optional<Curso> flyerPrincipal = cursoService.obtenerFlyerPrincipal();
        assertTrue(flyerPrincipal.isPresent());
        assertEquals(guardado.getId(), flyerPrincipal.get().getId());
        assertEquals("Curso Especializado con Flyer", flyerPrincipal.get().getNombre());

        // Alternar publicación a false (desactivar flyer de la portada)
        cursoService.alternarPublicacionFlyer(guardado.getId(), false);
        Curso actualizado = cursoRepository.findById(guardado.getId()).orElseThrow();
        assertFalse(actualizado.getFlyerPublicado());
    }

    @Test
    @DisplayName("Un curso en BORRADOR no debe proyectarse como flyer principal en el Inicio")
    void testCursoBorradorNoApareceEnFlyerPrincipal() {
        AreaInvestigacion area = areaRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("No hay áreas en la BD de prueba"));

        CursoDTO dto = new CursoDTO();
        dto.setNombre("Curso Borrador Oculto");
        dto.setDescripcion("Detalles de prueba borrador");
        dto.setAreaInvestigacionId(area.getId());
        dto.setDocenteResponsable("Docente Borrador");
        dto.setPrecio(new BigDecimal("100.00"));
        dto.setCuposTotales(20);
        dto.setDuracion("20 horas");
        dto.setCreditos(2);
        dto.setEstado(EstadoCurso.BORRADOR);
        dto.setFlyerUrl("https://res.cloudinary.com/fq7lbg0c/image/upload/v12345/flyer_borrador.jpg");
        dto.setFlyerPublicado(true);

        Curso guardado = cursoService.guardarOActualizar(dto);

        // Al ser BORRADOR, el servicio debe despublicar automáticamente el flyer
        assertFalse(guardado.getFlyerPublicado());

        // Y no debe figurar como flyer principal
        Optional<Curso> flyer = cursoService.obtenerFlyerPrincipal();
        if (flyer.isPresent()) {
            assertNotEquals(guardado.getId(), flyer.get().getId(), "Un curso en BORRADOR no debe ser el flyer principal");
        }
    }

    @Test
    @DisplayName("Debe permitir eliminar un curso que no tenga alumnos inscritos")
    void testEliminarCursoSinInscritos() {
        AreaInvestigacion area = areaRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("No hay áreas en la BD de prueba"));

        CursoDTO dto = new CursoDTO();
        dto.setNombre("Curso de Prueba a Eliminar");
        dto.setDescripcion("Curso temporal que será eliminado");
        dto.setAreaInvestigacionId(area.getId());
        dto.setDocenteResponsable("Docente Test");
        dto.setPrecio(new BigDecimal("50.00"));
        dto.setCuposTotales(15);
        dto.setDuracion("10 horas");
        dto.setCreditos(1);
        dto.setEstado(EstadoCurso.BORRADOR);

        Curso creado = cursoService.guardarOActualizar(dto);
        Long cursoId = creado.getId();
        assertNotNull(cursoId);

        // Eliminar curso
        cursoService.eliminar(cursoId);

        // Verificar que ya no existe en la base de datos
        assertTrue(cursoRepository.findById(cursoId).isEmpty());
    }

    @Test
    @DisplayName("El curso recién creado con flyer debe desplazar al anterior como flyer principal (createdAt DESC)")
    void testRecienCreadoDesplazaAnterior() throws InterruptedException {
        AreaInvestigacion area = areaRepository.findAll().stream().findFirst().orElseThrow();

        CursoDTO dto1 = new CursoDTO();
        dto1.setNombre("Curso Primer Evento");
        dto1.setDescripcion("Descripción primer evento");
        dto1.setAreaInvestigacionId(area.getId());
        dto1.setDocenteResponsable("Docente 1");
        dto1.setPrecio(new BigDecimal("100.00"));
        dto1.setCuposTotales(20);
        dto1.setDuracion("20 horas");
        dto1.setCreditos(2);
        dto1.setEstado(EstadoCurso.PUBLICADO);
        dto1.setFlyerUrl("https://res.cloudinary.com/fq7lbg0c/image/upload/v12345/flyer_1.jpg");
        dto1.setFlyerPublicado(true);

        Curso c1 = cursoService.guardarOActualizar(dto1);
        assertEquals(c1.getId(), cursoService.obtenerFlyerPrincipal().orElseThrow().getId());

        Thread.sleep(50);

        CursoDTO dto2 = new CursoDTO();
        dto2.setNombre("Curso Segundo Evento Recién Creado");
        dto2.setDescripcion("Descripción segundo evento");
        dto2.setAreaInvestigacionId(area.getId());
        dto2.setDocenteResponsable("Docente 2");
        dto2.setPrecio(new BigDecimal("120.00"));
        dto2.setCuposTotales(25);
        dto2.setDuracion("30 horas");
        dto2.setCreditos(3);
        dto2.setEstado(EstadoCurso.PUBLICADO);
        dto2.setFlyerUrl("https://res.cloudinary.com/fq7lbg0c/image/upload/v12345/flyer_2.jpg");
        dto2.setFlyerPublicado(true);

        Curso c2 = cursoService.guardarOActualizar(dto2);

        // El segundo curso (recién creado) debe ocupar la posición del flyer principal
        assertEquals(c2.getId(), cursoService.obtenerFlyerPrincipal().orElseThrow().getId());
    }

    @Test
    @DisplayName("Debe permitir eliminar un curso en estado BORRADOR que cuenta con participantes inscritos")
    void testEliminarCursoEnBorradorConInscritos() {
        AreaInvestigacion area = areaRepository.findAll().stream().findFirst().orElseThrow();
        Usuario usuario = obtenerOUsuarioPrueba();

        CursoDTO dto = new CursoDTO();
        dto.setNombre("Curso Borrador con Inscritos");
        dto.setDescripcion("Curso para validar eliminación en borrador");
        dto.setAreaInvestigacionId(area.getId());
        dto.setDocenteResponsable("Docente Test");
        dto.setPrecio(new BigDecimal("80.00"));
        dto.setCuposTotales(20);
        dto.setDuracion("15 horas");
        dto.setCreditos(2);
        dto.setEstado(EstadoCurso.BORRADOR);

        Curso creado = cursoService.guardarOActualizar(dto);
        Long cursoId = creado.getId();

        Inscripcion inscripcion = new Inscripcion();
        inscripcion.setCurso(creado);
        inscripcion.setUsuario(usuario);
        inscripcion.setEstado(EstadoInscripcion.PENDIENTE_PAGO);
        inscripcion = inscripcionRepository.save(inscripcion);
        Long inscripcionId = inscripcion.getId();

        assertDoesNotThrow(() -> cursoService.eliminar(cursoId));

        assertTrue(cursoRepository.findById(cursoId).isEmpty(), "El curso borrador debe ser eliminado");
        assertTrue(inscripcionRepository.findById(inscripcionId).isEmpty(), "Las inscripciones del curso deben eliminarse");
    }

    @Test
    @DisplayName("Debe permitir eliminar un curso en estado FINALIZADO que cuenta con participantes inscritos")
    void testEliminarCursoFinalizadoConInscritos() {
        AreaInvestigacion area = areaRepository.findAll().stream().findFirst().orElseThrow();
        Usuario usuario = obtenerOUsuarioPrueba();

        CursoDTO dto = new CursoDTO();
        dto.setNombre("Curso Finalizado con Inscritos");
        dto.setDescripcion("Curso terminado que se desea depurar");
        dto.setAreaInvestigacionId(area.getId());
        dto.setDocenteResponsable("Docente Test");
        dto.setPrecio(new BigDecimal("100.00"));
        dto.setCuposTotales(20);
        dto.setDuracion("25 horas");
        dto.setCreditos(2);
        dto.setEstado(EstadoCurso.FINALIZADO);

        Curso creado = cursoService.guardarOActualizar(dto);
        Long cursoId = creado.getId();

        Inscripcion inscripcion = new Inscripcion();
        inscripcion.setCurso(creado);
        inscripcion.setUsuario(usuario);
        inscripcion.setEstado(EstadoInscripcion.APROBADA);
        inscripcion = inscripcionRepository.save(inscripcion);
        Long inscripcionId = inscripcion.getId();

        assertDoesNotThrow(() -> cursoService.eliminar(cursoId));

        assertTrue(cursoRepository.findById(cursoId).isEmpty(), "El curso finalizado debe ser eliminado");
        assertTrue(inscripcionRepository.findById(inscripcionId).isEmpty(), "Las inscripciones del curso finalizado deben eliminarse");
    }

    @Test
    @DisplayName("No debe permitir eliminar un curso en estado PUBLICADO")
    void testNoPermitirEliminarCursoPublicado() {
        AreaInvestigacion area = areaRepository.findAll().stream().findFirst().orElseThrow();

        CursoDTO dto = new CursoDTO();
        dto.setNombre("Curso Activo en Venta");
        dto.setDescripcion("Curso publicado actualmente en catálogo");
        dto.setAreaInvestigacionId(area.getId());
        dto.setDocenteResponsable("Docente Test");
        dto.setPrecio(new BigDecimal("150.00"));
        dto.setCuposTotales(30);
        dto.setDuracion("40 horas");
        dto.setCreditos(3);
        dto.setEstado(EstadoCurso.PUBLICADO);

        Curso creado = cursoService.guardarOActualizar(dto);
        Long cursoId = creado.getId();

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> cursoService.eliminar(cursoId));
        assertTrue(ex.getMessage().contains("PUBLICADO"));
        assertTrue(cursoRepository.findById(cursoId).isPresent(), "El curso publicado debe seguir existiendo");
    }

    private Usuario obtenerOUsuarioPrueba() {
        return usuarioRepository.findAll().stream().findFirst().orElseGet(() -> {
            Usuario u = new Usuario();
            u.setNombreCompleto("Usuario Test Eliminación");
            u.setCorreo("test_eliminar_" + System.currentTimeMillis() + "@test.com");
            u.setTipoDocumento(TipoDocumento.DNI);
            u.setNumeroDocumento("99" + (System.currentTimeMillis() % 1000000));
            u.setPasswordHash("$2a$10$abcdefghijklmnopqrstuvwxyz123456");
            u.setRol(Rol.PARTICIPANTE);
            return usuarioRepository.save(u);
        });
    }
}
