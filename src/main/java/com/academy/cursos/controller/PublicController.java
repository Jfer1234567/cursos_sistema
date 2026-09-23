package com.academy.cursos.controller;

import com.academy.cursos.repository.AreaInvestigacionRepository;
import com.academy.cursos.repository.LineaInvestigacionRepository;
import com.academy.cursos.service.CursoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PublicController {

    private final LineaInvestigacionRepository lineaRepo;
    private final AreaInvestigacionRepository areaRepo;
    private final CursoService cursoService;

    public PublicController(
            LineaInvestigacionRepository lineaRepo,
            AreaInvestigacionRepository areaRepo,
            CursoService cursoService) {
        this.lineaRepo = lineaRepo;
        this.areaRepo = areaRepo;
        this.cursoService = cursoService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("lineas", lineaRepo.findAllByOrderByOrdenAsc());
        model.addAttribute("cursosDestacados", cursoService.listarDestacados());
        return "public/index";
    }

    @GetMapping("/quienes-somos")
    public String quienesSomos() {
        return "public/quienes-somos";
    }

    @GetMapping("/lineas-investigacion")
    public String lineasInvestigacion(Model model) {
        model.addAttribute("lineas", lineaRepo.findAllByOrderByOrdenAsc());
        model.addAttribute("areas", areaRepo.findAll());
        return "public/lineas-investigacion";
    }

    @GetMapping("/contacto")
    public String contacto() {
        return "public/contacto";
    }
}
