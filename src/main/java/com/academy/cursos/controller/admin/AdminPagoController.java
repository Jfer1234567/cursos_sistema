package com.academy.cursos.controller.admin;

import com.academy.cursos.model.Usuario;
import com.academy.cursos.repository.CertificadoRepository;
import com.academy.cursos.service.PagoService;
import com.academy.cursos.service.UsuarioService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminPagoController {

    private final PagoService pagoService;
    private final UsuarioService usuarioService;
    private final CertificadoRepository certificadoRepo;

    public AdminPagoController(
            PagoService pagoService,
            UsuarioService usuarioService,
            CertificadoRepository certificadoRepo) {
        this.pagoService = pagoService;
        this.usuarioService = usuarioService;
        this.certificadoRepo = certificadoRepo;
    }

    @PostMapping("/admin/pagos/{id}/aprobar")
    public String aprobarPago(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        Usuario admin = usuarioService.buscarPorCorreo(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Admin no encontrado"));

        try {
            pagoService.aprobarPago(id, admin.getId());
            redirectAttributes.addFlashAttribute("successMsg", "El pago ha sido APROBADO exitosamente y el participante ya tiene acceso al curso.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Error al aprobar pago: " + e.getMessage());
        }
        return "redirect:/admin/inscripciones/pendientes";
    }

    @PostMapping("/admin/pagos/{id}/rechazar")
    public String rechazarPago(
            @PathVariable Long id,
            @RequestParam String motivo,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        Usuario admin = usuarioService.buscarPorCorreo(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Admin no encontrado"));

        try {
            pagoService.rechazarPago(id, admin.getId(), motivo);
            redirectAttributes.addFlashAttribute("successMsg", "El pago ha sido RECHAZADO con las observaciones registradas.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Error al rechazar pago: " + e.getMessage());
        }
        return "redirect:/admin/inscripciones/pendientes";
    }

    @GetMapping("/admin/certificados")
    public String listarCertificados(Model model) {
        model.addAttribute("certificados", certificadoRepo.findAll());
        return "admin/certificados/lista";
    }
}
