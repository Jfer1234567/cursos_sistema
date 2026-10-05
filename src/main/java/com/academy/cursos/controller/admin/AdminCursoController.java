package com.academy.cursos.controller.admin;

import com.academy.cursos.dto.CursoDTO;
import com.academy.cursos.model.Curso;
import com.academy.cursos.model.Inscripcion;
import com.academy.cursos.model.Pago;
import com.academy.cursos.model.Usuario;
import com.academy.cursos.model.enums.EstadoCurso;
import com.academy.cursos.repository.AreaInvestigacionRepository;
import com.academy.cursos.service.CursoService;
import com.academy.cursos.service.InscripcionService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
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
    public String listarCursos(
            @RequestParam(name = "page", defaultValue = "0") int page,
            Model model) {
        int pageSize = 4;
        Page<Curso> cursosPage = cursoService.listarPaginado(page, pageSize);
        model.addAttribute("cursosPage", cursosPage);
        model.addAttribute("cursos", cursosPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", cursosPage.getTotalPages());
        model.addAttribute("totalElements", cursosPage.getTotalElements());
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

        model.addAttribute("cursoDTO", CursoDTO.fromEntity(curso));
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

    @PostMapping("/{id}/flyer/toggle")
    public String alternarPublicacionFlyer(
            @PathVariable Long id,
            @RequestParam boolean publicado,
            RedirectAttributes redirectAttributes) {
        try {
            cursoService.alternarPublicacionFlyer(id, publicado);
            redirectAttributes.addFlashAttribute("successMsg", publicado
                    ? "Flyer publicado en la portada de Inicio con éxito."
                    : "Flyer retirado de la portada de Inicio.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Error al actualizar estado del flyer: " + e.getMessage());
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

    @GetMapping("/{id}/exportar-csv")
    public void exportarPadronCsv(@PathVariable Long id, HttpServletResponse response) throws IOException {
        Curso curso = cursoService.obtenerPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Curso no encontrado"));
        List<Inscripcion> inscripciones = inscripcionService.listarPorCurso(id);

        String safeNombre = curso.getNombre().replaceAll("[^a-zA-Z0-9_-]", "_");
        String filename = "padron_" + safeNombre + ".csv";
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

        // Write UTF-8 BOM so Microsoft Excel recognizes UTF-8 directly
        OutputStream os = response.getOutputStream();
        os.write(0xEF);
        os.write(0xBB);
        os.write(0xBF);

        PrintWriter writer = new PrintWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
        writer.println("N°;Apellidos y Nombres;Correo;Teléfono;Tipo Doc;N° Documento;Institución;Estado Inscripción;Monto Pagado;Código Operación;Fecha Inscripción");

        int num = 1;
        for (Inscripcion ins : inscripciones) {
            Usuario u = ins.getUsuario();
            Pago p = ins.getPago();
            String monto = (p != null && p.getMonto() != null) ? p.getMonto().toString() : "0.00";
            String nroOp = (p != null && p.getReferenciaExterna() != null) ? p.getReferenciaExterna() : "-";
            String tel = (u.getTelefono() != null) ? u.getTelefono() : "-";
            String tipoDoc = (u.getTipoDocumento() != null) ? u.getTipoDocumento().name() : "-";
            String dni = (u.getNumeroDocumento() != null) ? u.getNumeroDocumento() : "-";
            String inst = (u.getInstitucionProcedencia() != null) ? u.getInstitucionProcedencia().replace(";", ",") : "-";
            String nom = (u.getNombreCompleto() != null) ? u.getNombreCompleto().replace(";", ",") : "-";
            String estadoIns = (ins.getEstado() != null) ? ins.getEstado().getEtiqueta() : "-";
            String fecha = (ins.getFechaInscripcion() != null) ? ins.getFechaInscripcion().toString() : "-";

            writer.println(String.format("%d;\"%s\";\"%s\";\"%s\";\"%s\";\"%s\";\"%s\";\"%s\";\"%s\";\"%s\";\"%s\"",
                    num++,
                    nom,
                    u.getCorreo(),
                    tel,
                    tipoDoc,
                    dni,
                    inst,
                    estadoIns,
                    monto,
                    nroOp,
                    fecha
            ));
        }
        writer.flush();
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
