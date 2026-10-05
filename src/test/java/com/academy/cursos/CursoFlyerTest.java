package com.academy.cursos;

import com.academy.cursos.dto.CursoDTO;
import com.academy.cursos.model.AreaInvestigacion;
import com.academy.cursos.model.Curso;
import com.academy.cursos.model.enums.EstadoCurso;
import com.academy.cursos.repository.AreaInvestigacionRepository;
import com.academy.cursos.repository.CursoRepository;
import com.academy.cursos.service.CursoService;
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
}
