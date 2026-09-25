package com.academy.cursos;

import com.academy.cursos.model.*;
import com.academy.cursos.model.enums.EstadoCurso;
import com.academy.cursos.model.enums.EstadoInscripcion;
import com.academy.cursos.model.enums.Rol;
import com.academy.cursos.model.enums.TipoDocumento;
import com.academy.cursos.service.CertificadoService;
import com.academy.cursos.util.CertificadoPdfGenerator;
import com.lowagie.text.DocumentException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CertificadoServiceTest {

    @Autowired
    private CertificadoService certificadoService;

    @Test
    void testGenerarCertificadoYPdf() throws DocumentException {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setId(100L);
        usuario.setNombreCompleto("Juan Alberto Pérez");
        usuario.setCorreo("juan@example.com");
        usuario.setNumeroDocumento("77889900");
        usuario.setTipoDocumento(TipoDocumento.DNI);
        usuario.setRol(Rol.PARTICIPANTE);

        LineaInvestigacion linea = new LineaInvestigacion();
        linea.setNombre("Ciencias de la Computación");

        AreaInvestigacion area = new AreaInvestigacion();
        area.setNombre("Inteligencia Artificial");
        area.setLineaInvestigacion(linea);

        Curso curso = new Curso();
        curso.setId(200L);
        curso.setNombre("Machine Learning Especializado");
        curso.setAreaInvestigacion(area);
        curso.setDocenteResponsable("Dr. Leonid Alemán");
        curso.setDuracion("40 horas académicas");
        curso.setPrecio(BigDecimal.valueOf(150.0));
        curso.setCuposTotales(30);
        curso.setCuposDisponibles(30);
        curso.setEstado(EstadoCurso.PUBLICADO);

        Inscripcion inscripcion = new Inscripcion();
        inscripcion.setId(300L);
        inscripcion.setUsuario(usuario);
        inscripcion.setCurso(curso);
        inscripcion.setEstado(EstadoInscripcion.COMPLETADA);

        Certificado cert = new Certificado();
        cert.setId(1L);
        cert.setInscripcion(inscripcion);
        cert.setCodigoVerificacion("IIICCD-TEST99");
        cert.setFechaEmision(LocalDate.now());
        cert.setNombreParticipante(usuario.getNombreCompleto());
        cert.setNombreCurso(curso.getNombre());
        cert.setLineaInvestigacion(linea.getNombre());
        cert.setAreaInvestigacion(area.getNombre());
        cert.setDocente(curso.getDocenteResponsable());
        cert.setDuracion(curso.getDuracion());
        cert.setCreditos(3);

        // Act: Generación de PDF con OpenPDF
        byte[] pdfBytes = CertificadoPdfGenerator.generarCertificadoPdf(cert);

        // Assert
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 15000, "El PDF generado con marcas de agua y ornamentos debe tener un tamaño sustancial");
        assertEquals("IIICCD-TEST99", cert.getCodigoVerificacion());
        assertTrue(cert.getCodigoVerificacion().startsWith("IIICCD-"));
        assertEquals(3, cert.getCreditos());
        try {
            java.nio.file.Files.createDirectories(java.nio.file.Path.of("build"));
            java.nio.file.Files.write(java.nio.file.Path.of("build/sample_certificado_generado.pdf"), pdfBytes);
        } catch (Exception ignored) {}
    }
}
