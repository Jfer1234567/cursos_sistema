package com.academy.cursos.controller;

import com.academy.cursos.model.Certificado;
import com.academy.cursos.model.Inscripcion;
import com.academy.cursos.model.Usuario;
import com.academy.cursos.service.CertificadoService;
import com.academy.cursos.service.InscripcionService;
import com.academy.cursos.service.UsuarioService;
import com.lowagie.text.DocumentException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class CertificadoController {

    private final CertificadoService certificadoService;
    private final InscripcionService inscripcionService;
    private final UsuarioService usuarioService;

    public CertificadoController(
            CertificadoService certificadoService,
            InscripcionService inscripcionService,
            UsuarioService usuarioService) {
        this.certificadoService = certificadoService;
        this.inscripcionService = inscripcionService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/participante/certificado/{inscripcionId}")
    public String verCertificado(
            @PathVariable Long inscripcionId,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {

        Usuario usuario = usuarioService.buscarPorCorreo(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        Inscripcion inscripcion = inscripcionService.obtenerPorId(inscripcionId)
                .orElseThrow(() -> new IllegalArgumentException("Inscripción no encontrada"));

        if (!inscripcion.getUsuario().getId().equals(usuario.getId()) &&
                !usuario.getRol().name().equals("ADMIN")) {
            return "redirect:/participante/dashboard";
        }

        Certificado certificado = certificadoService.obtenerPorInscripcionId(inscripcionId)
                .orElseThrow(() -> new IllegalArgumentException("El certificado aún no ha sido emitido"));

        model.addAttribute("certificado", certificado);
        model.addAttribute("inscripcion", inscripcion);
        return "participante/certificado";
    }

    @GetMapping("/participante/certificado/{inscripcionId}/pdf")
    public ResponseEntity<byte[]> descargarCertificadoPdf(
            @PathVariable Long inscripcionId,
            @AuthenticationPrincipal UserDetails userDetails) throws DocumentException {

        Usuario usuario = usuarioService.buscarPorCorreo(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        Inscripcion inscripcion = inscripcionService.obtenerPorId(inscripcionId)
                .orElseThrow(() -> new IllegalArgumentException("Inscripción no encontrada"));

        if (!inscripcion.getUsuario().getId().equals(usuario.getId()) &&
                !usuario.getRol().name().equals("ADMIN")) {
            return ResponseEntity.status(403).build();
        }

        Certificado certificado = certificadoService.obtenerPorInscripcionId(inscripcionId)
                .orElseThrow(() -> new IllegalArgumentException("Certificado no emitido"));

        byte[] pdfBytes = certificadoService.descargarPdf(certificado.getId());

        String filename = "Certificado-" + certificado.getCodigoVerificacion() + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @GetMapping({"/verificar", "/verificar/certificado"})
    public String verificarPublico(
            @RequestParam(required = false) String codigo,
            Model model) {

        if (codigo != null && !codigo.trim().isEmpty()) {
            Optional<Certificado> certOpt = certificadoService.obtenerPorCodigo(codigo);
            if (certOpt.isPresent()) {
                model.addAttribute("certificado", certOpt.get());
                model.addAttribute("encontrado", true);
            } else {
                model.addAttribute("errorMsg", "No se encontró ningún certificado válido con el código: " + codigo.trim());
                model.addAttribute("encontrado", false);
            }
            model.addAttribute("codigoBuscado", codigo.trim());
        }

        return "verificar/certificado";
    }

    @GetMapping("/verificar/certificado/{codigo}/pdf")
    public ResponseEntity<byte[]> descargarCertificadoPublicoPdf(@PathVariable String codigo) throws DocumentException {
        Certificado certificado = certificadoService.obtenerPorCodigo(codigo)
                .orElseThrow(() -> new IllegalArgumentException("Certificado no encontrado"));

        byte[] pdfBytes = certificadoService.descargarPdf(certificado.getId());
        String filename = "Certificado-" + certificado.getCodigoVerificacion() + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
