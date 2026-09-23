package com.academy.cursos.controller.admin;

import com.academy.cursos.model.Inscripcion;
import com.academy.cursos.service.InscripcionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/inscripciones")
public class AdminInscripcionController {

    private final InscripcionService inscripcionService;

    public AdminInscripcionController(InscripcionService inscripcionService) {
        this.inscripcionService = inscripcionService;
    }

    @GetMapping("/pendientes")
    public String listarPendientes(Model model) {
        model.addAttribute("inscripciones", inscripcionService.listarPendientesVerificacion());
        return "admin/inscripciones/pendientes";
    }

    @GetMapping("/{id}")
    public String verDetalle(@PathVariable Long id, Model model) {
        Inscripcion inscripcion = inscripcionService.obtenerPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Inscripción no encontrada"));
        model.addAttribute("inscripcion", inscripcion);
        return "admin/inscripciones/detalle";
    }
}
