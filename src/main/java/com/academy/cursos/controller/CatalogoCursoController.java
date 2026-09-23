package com.academy.cursos.controller;

import com.academy.cursos.model.Curso;
import com.academy.cursos.repository.LineaInvestigacionRepository;
import com.academy.cursos.service.CursoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CatalogoCursoController {

    private final CursoService cursoService;
    private final LineaInvestigacionRepository lineaRepo;

    public CatalogoCursoController(CursoService cursoService, LineaInvestigacionRepository lineaRepo) {
        this.cursoService = cursoService;
        this.lineaRepo = lineaRepo;
    }

    @GetMapping("/catalogo")
    public String catalogo(@RequestParam(required = false) Long lineaId, Model model) {
        model.addAttribute("lineas", lineaRepo.findAllByOrderByOrdenAsc());
        model.addAttribute("lineaSeleccionada", lineaId);
        model.addAttribute("cursos", cursoService.listarPublicados(lineaId));
        return "public/catalogo";
    }

    @GetMapping("/cursos/{id}")
    public String detalleCurso(@PathVariable Long id, Model model) {
        Curso curso = cursoService.obtenerPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Curso no encontrado"));
        model.addAttribute("curso", curso);
        return "public/curso-detalle";
    }
}
