package com.academy.cursos.controller.admin;

import com.academy.cursos.model.ConfigPago;
import com.academy.cursos.model.Curso;
import com.academy.cursos.service.ConfigPagoService;
import com.academy.cursos.service.CursoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

@Controller
public class AdminFlyerController {

    private final CursoService cursoService;
    private final ConfigPagoService configPagoService;

    public AdminFlyerController(CursoService cursoService, ConfigPagoService configPagoService) {
        this.cursoService = cursoService;
        this.configPagoService = configPagoService;
    }

    @GetMapping("/admin/flyer")
    public String disenoFlyerGeneral(Model model) {
        List<Curso> cursos = cursoService.listarTodos();
        ConfigPago configPago = configPagoService.obtenerConfigActiva();

        Curso cursoInicial = cursos.isEmpty() ? null : cursos.get(0);

        model.addAttribute("cursos", cursos);
        model.addAttribute("cursoSeleccionado", cursoInicial);
        model.addAttribute("configPago", configPago);
        return "admin/flyer/editor";
    }

    @GetMapping("/admin/flyer/curso/{id}")
    public String disenoFlyerPorCurso(@PathVariable Long id, Model model) {
        List<Curso> cursos = cursoService.listarTodos();
        ConfigPago configPago = configPagoService.obtenerConfigActiva();
        Optional<Curso> cursoOpt = cursoService.obtenerPorId(id);

        Curso curso = cursoOpt.orElse(cursos.isEmpty() ? null : cursos.get(0));

        model.addAttribute("cursos", cursos);
        model.addAttribute("cursoSeleccionado", curso);
        model.addAttribute("configPago", configPago);
        return "admin/flyer/editor";
    }

    @GetMapping("/admin/cursos/{id}/flyer")
    public String redirigirFlyerCurso(@PathVariable Long id) {
        return "redirect:/admin/flyer/curso/" + id;
    }
}
