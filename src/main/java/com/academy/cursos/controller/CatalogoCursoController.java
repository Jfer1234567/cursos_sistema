package com.academy.cursos.controller;

import com.academy.cursos.model.Curso;
import com.academy.cursos.model.Inscripcion;
import com.academy.cursos.service.CursoService;
import com.academy.cursos.service.InscripcionService;
import com.academy.cursos.service.InvestigacionService;
import com.academy.cursos.service.UsuarioService;
import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class CatalogoCursoController {

    private final CursoService cursoService;
    private final InvestigacionService investigacionService;
    private final InscripcionService inscripcionService;
    private final UsuarioService usuarioService;

    public CatalogoCursoController(
            CursoService cursoService,
            InvestigacionService investigacionService,
            InscripcionService inscripcionService,
            UsuarioService usuarioService) {
        this.cursoService = cursoService;
        this.investigacionService = investigacionService;
        this.inscripcionService = inscripcionService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/catalogo")
    public String catalogo(
            @RequestParam(required = false) Long lineaId,
            Principal principal,
            Model model) {

        model.addAttribute("lineas", investigacionService.listarLineas());
        model.addAttribute("lineaSeleccionada", lineaId);
        model.addAttribute("cursos", cursoService.listarPublicados(lineaId));

        Map<Long, Inscripcion> misInscripcionesMap = new HashMap<>();
        if (principal != null) {
            usuarioService.buscarPorCorreo(principal.getName()).ifPresent(usuario -> {
                List<Inscripcion> misInscripciones = inscripcionService.listarPorUsuario(usuario.getId());
                for (Inscripcion ins : misInscripciones) {
                    misInscripcionesMap.put(ins.getCurso().getId(), ins);
                }
            });
        }
        model.addAttribute("misInscripcionesMap", misInscripcionesMap);

        return "public/catalogo";
    }

    @GetMapping("/cursos/{id}")
    public String detalleCurso(
            @PathVariable Long id,
            Principal principal,
            Model model) {

        Curso curso = cursoService.obtenerPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Curso no encontrado"));

        model.addAttribute("curso", curso);

        if (principal != null) {
            usuarioService.buscarPorCorreo(principal.getName()).ifPresent(usuario -> {
                Optional<Inscripcion> insOpt = inscripcionService.obtenerPorUsuarioYCurso(usuario.getId(), id);
                insOpt.ifPresent(ins -> model.addAttribute("miInscripcion", ins));
            });
        }

        return "public/curso-detalle";
    }
}
