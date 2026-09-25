package com.academy.cursos.controller.admin;

import com.academy.cursos.model.MensajeContacto;
import com.academy.cursos.service.MensajeContactoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/mensajes")
public class AdminMensajeController {

    private final MensajeContactoService mensajeService;

    public AdminMensajeController(MensajeContactoService mensajeService) {
        this.mensajeService = mensajeService;
    }

    @GetMapping
    public String listarMensajes(
            @RequestParam(required = false) String filtro,
            Model model) {

        List<MensajeContacto> mensajes = mensajeService.listarMensajes(filtro);
        long noLeidos = mensajeService.contarNoLeidos();

        model.addAttribute("mensajes", mensajes);
        model.addAttribute("noLeidos", noLeidos);
        model.addAttribute("filtro", filtro != null ? filtro.trim() : "");

        return "admin/mensajes/lista";
    }

    @GetMapping("/{id}")
    public String verDetalle(
            @PathVariable Long id,
            Model model) {

        MensajeContacto mensaje = mensajeService.obtenerPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Mensaje no encontrado"));

        // Al abrir la vista de detalle, marcar como leído si estaba pendiente
        if (!Boolean.TRUE.equals(mensaje.getLeido())) {
            mensajeService.marcarComoLeido(id);
            mensaje.setLeido(true);
        }

        model.addAttribute("mensaje", mensaje);
        return "admin/mensajes/detalle";
    }

    @PostMapping("/{id}/marcar-leido")
    public String marcarLeido(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        mensajeService.marcarComoLeido(id);
        redirectAttributes.addFlashAttribute("successMsg", "Mensaje marcado como leído.");
        return "redirect:/admin/mensajes";
    }

    @PostMapping("/{id}/marcar-respondido")
    public String marcarRespondido(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        mensajeService.marcarComoRespondido(id);
        redirectAttributes.addFlashAttribute("successMsg", "Mensaje marcado como respondido y atendido.");
        return "redirect:/admin/mensajes/" + id;
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        mensajeService.eliminarMensaje(id);
        redirectAttributes.addFlashAttribute("successMsg", "Mensaje eliminado de la bandeja.");
        return "redirect:/admin/mensajes";
    }
}
