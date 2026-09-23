package com.academy.cursos.controller.admin;

import com.academy.cursos.dto.CursoDTO;
import com.academy.cursos.model.Curso;
import com.academy.cursos.model.Inscripcion;
import com.academy.cursos.model.enums.EstadoCurso;
import com.academy.cursos.repository.AreaInvestigacionRepository;
import com.academy.cursos.service.CursoService;
import com.academy.cursos.service.InscripcionService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/cursos")
public class AdminCursoController {

    private final CursoService cursoService;
    private final AreaInvestigacionRepository areaRepo;
    private final InscripcionService inscripcionService;

    public AdminCursoController(
            CursoService cursoService,
            AreaInvestigacionRepository areaRepo,
            InscripcionService inscripcionService) {
        this.cursoService = cursoService;
        this.areaRepo = areaRepo;
        this.inscripcionService = inscripcionService;
    }

    @GetMapping
    public String listarCursos(Model model) {
        model.addAttribute("cursos", cursoService.listarTodos());
        return "admin/cursos/lista";
    }

    @GetMapping("/nuevo")
    public String nuevoCurso(Model model) {
        if (!model.containsAttribute("cursoDTO")) {
            model.addAttribute("cursoDTO", new CursoDTO());
        }
        model.addAttribute("areas", areaRepo.findAll());
        model.addAttribute("estados", EstadoCurso.values());
        return "admin/cursos/form";
    }

    @PostMapping("/guardar")
    public String guardarCurso(
            @Valid @ModelAttribute("cursoDTO") CursoDTO cursoDTO,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("areas", areaRepo.findAll());
            model.addAttribute("estados", EstadoCurso.values());
            return "admin/cursos/form";
        }

        try {
            cursoService.guardarOActualizar(cursoDTO);
            redirectAttributes.addFlashAttribute("successMsg", "Curso guardado correctamente.");
            return "redirect:/admin/cursos";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Error al guardar el curso: " + e.getMessage());
            return "redirect:/admin/cursos";
        }
    }

    @GetMapping("/{id}/editar")
    public String editarCurso(@PathVariable Long id, Model model) {
        Curso curso = cursoService.obtenerPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Curso no encontrado"));

        CursoDTO dto = new CursoDTO();
        dto.setId(curso.getId());
        dto.setNombre(curso.getNombre());
        dto.setDescripcionCorta(curso.getDescripcionCorta());
        dto.setDescripcion(curso.getDescripcion());
        dto.setAreaInvestigacionId(curso.getAreaInvestigacion().getId());
        dto.setDuracion(curso.getDuracion());
        dto.setDocenteResponsable(curso.getDocenteResponsable());
        dto.setPrecio(curso.getPrecio());
        dto.setCuposTotales(curso.getCuposTotales());
        dto.setEstado(curso.getEstado());
        dto.setFechaInicio(curso.getFechaInicio());
        dto.setFechaFin(curso.getFechaFin());
        dto.setImagenUrl(curso.getImagenUrl());
        dto.setEnlaceClase(curso.getEnlaceClase());

        model.addAttribute("cursoDTO", dto);
        model.addAttribute("areas", areaRepo.findAll());
        model.addAttribute("estados", EstadoCurso.values());
        return "admin/cursos/form";
    }

    @PostMapping("/{id}/estado")
    public String cambiarEstado(
            @PathVariable Long id,
            @RequestParam EstadoCurso estado,
            RedirectAttributes redirectAttributes) {
        try {
            cursoService.cambiarEstado(id, estado);
            redirectAttributes.addFlashAttribute("successMsg", "Estado del curso actualizado.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Error al actualizar estado: " + e.getMessage());
        }
        return "redirect:/admin/cursos";
    }

    @GetMapping("/{id}/detalle")
    public String detalleCurso(@PathVariable Long id, Model model) {
        Curso curso = cursoService.obtenerPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Curso no encontrado"));
        List<Inscripcion> inscripciones = inscripcionService.listarPorCurso(id);

        model.addAttribute("curso", curso);
        model.addAttribute("inscripciones", inscripciones);
        return "admin/cursos/detalle";
    }

    @PostMapping("/inscripciones/{inscripcionId}/completar")
    public String marcarInscripcionCompletada(
            @PathVariable Long inscripcionId,
            @RequestParam Long cursoId,
            RedirectAttributes redirectAttributes) {
        try {
            inscripcionService.marcarCompletada(inscripcionId);
            redirectAttributes.addFlashAttribute("successMsg", "Inscripción marcada como completada y certificado emitido con éxito.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Error al completar inscripción: " + e.getMessage());
        }
        return "redirect:/admin/cursos/" + cursoId + "/detalle";
    }
}
