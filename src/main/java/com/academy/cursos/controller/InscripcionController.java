package com.academy.cursos.controller;

import com.academy.cursos.dto.PagoDTO;
import com.academy.cursos.model.ConfigPago;
import com.academy.cursos.model.Inscripcion;
import com.academy.cursos.model.Usuario;
import com.academy.cursos.service.ConfigPagoService;
import com.academy.cursos.service.InscripcionService;
import com.academy.cursos.service.PagoService;
import com.academy.cursos.service.UsuarioService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/inscripcion")
public class InscripcionController {

    private final InscripcionService inscripcionService;
    private final PagoService pagoService;
    private final ConfigPagoService configPagoService;
    private final UsuarioService usuarioService;

    public InscripcionController(
            InscripcionService inscripcionService,
            PagoService pagoService,
            ConfigPagoService configPagoService,
            UsuarioService usuarioService) {
        this.inscripcionService = inscripcionService;
        this.pagoService = pagoService;
        this.configPagoService = configPagoService;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/curso/{cursoId}")
    public String iniciarInscripcion(
            @PathVariable Long cursoId,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        Usuario usuario = usuarioService.buscarPorCorreo(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no autenticado"));

        try {
            Inscripcion inscripcion = inscripcionService.iniciarInscripcion(usuario.getId(), cursoId);
            return "redirect:/inscripcion/" + inscripcion.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", e.getMessage());
            return "redirect:/cursos/" + cursoId;
        }
    }

    @GetMapping("/{id}")
    public String verFormularioPago(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {

        Usuario usuario = usuarioService.buscarPorCorreo(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no autenticado"));

        Inscripcion inscripcion = inscripcionService.obtenerPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Inscripción no encontrada"));

        // Seguridad: verificar que pertenezca al usuario autenticado (o admin)
        if (!inscripcion.getUsuario().getId().equals(usuario.getId()) &&
                !usuario.getRol().name().equals("ADMIN")) {
            return "redirect:/participante/dashboard";
        }

        ConfigPago configPago = configPagoService.obtenerConfigActiva();

        model.addAttribute("inscripcion", inscripcion);
        model.addAttribute("configPago", configPago);
        model.addAttribute("pagoDTO", new PagoDTO());

        return "participante/inscripcion";
    }

    @PostMapping("/{id}/pago")
    public String procesarPago(
            @PathVariable Long id,
            @RequestParam("referenciaExterna") String referenciaExterna,
            @RequestParam("comprobante") MultipartFile comprobante,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        Usuario usuario = usuarioService.buscarPorCorreo(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no autenticado"));

        Inscripcion inscripcion = inscripcionService.obtenerPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Inscripción no encontrada"));

        if (!inscripcion.getUsuario().getId().equals(usuario.getId()) &&
                !usuario.getRol().name().equals("ADMIN")) {
            return "redirect:/participante/dashboard";
        }

        if (referenciaExterna == null || referenciaExterna.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMsg", "Debe ingresar el número de operación de Yape");
            return "redirect:/inscripcion/" + id;
        }

        if (comprobante == null || comprobante.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMsg", "Debe adjuntar la captura del comprobante o voucher de pago");
            return "redirect:/inscripcion/" + id;
        }

        try {
            pagoService.registrarPagoYape(id, referenciaExterna.trim(), comprobante);
            redirectAttributes.addFlashAttribute("successMsg", "Comprobante enviado exitosamente. Tu inscripción está pendiente de verificación.");
            return "redirect:/participante/dashboard";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Error al procesar el comprobante: " + e.getMessage());
            return "redirect:/inscripcion/" + id;
        }
    }
}
