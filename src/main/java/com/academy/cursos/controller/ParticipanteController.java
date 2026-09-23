package com.academy.cursos.controller;

import com.academy.cursos.model.Inscripcion;
import com.academy.cursos.model.Usuario;
import com.academy.cursos.service.InscripcionService;
import com.academy.cursos.service.UsuarioService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/participante")
public class ParticipanteController {

    private final InscripcionService inscripcionService;
    private final UsuarioService usuarioService;

    public ParticipanteController(InscripcionService inscripcionService, UsuarioService usuarioService) {
        this.inscripcionService = inscripcionService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Usuario usuario = usuarioService.buscarPorCorreo(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no autenticado"));

        List<Inscripcion> inscripciones = inscripcionService.listarPorUsuario(usuario.getId());

        model.addAttribute("usuario", usuario);
        model.addAttribute("inscripciones", inscripciones);
        return "participante/dashboard";
    }

    @GetMapping("/perfil")
    public String perfil(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Usuario usuario = usuarioService.buscarPorCorreo(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no autenticado"));

        model.addAttribute("usuario", usuario);
        return "participante/perfil";
    }

    @PostMapping("/perfil")
    public String actualizarPerfil(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String nombreCompleto,
            @RequestParam String telefono,
            @RequestParam String institucionProcedencia,
            RedirectAttributes redirectAttributes) {

        Usuario usuario = usuarioService.buscarPorCorreo(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no autenticado"));

        try {
            usuarioService.actualizarPerfil(usuario.getId(), nombreCompleto, telefono, institucionProcedencia);
            redirectAttributes.addFlashAttribute("successMsg", "Datos actualizados correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Error al actualizar perfil: " + e.getMessage());
        }

        return "redirect:/participante/perfil";
    }
}
