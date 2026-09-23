package com.academy.cursos.controller.admin;

import com.academy.cursos.model.enums.EstadoCurso;
import com.academy.cursos.model.enums.EstadoInscripcion;
import com.academy.cursos.model.enums.EstadoPago;
import com.academy.cursos.repository.CertificadoRepository;
import com.academy.cursos.repository.CursoRepository;
import com.academy.cursos.repository.InscripcionRepository;
import com.academy.cursos.repository.PagoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    private final CursoRepository cursoRepo;
    private final InscripcionRepository inscripcionRepo;
    private final PagoRepository pagoRepo;
    private final CertificadoRepository certificadoRepo;

    public AdminDashboardController(
            CursoRepository cursoRepo,
            InscripcionRepository inscripcionRepo,
            PagoRepository pagoRepo,
            CertificadoRepository certificadoRepo) {
        this.cursoRepo = cursoRepo;
        this.inscripcionRepo = inscripcionRepo;
        this.pagoRepo = pagoRepo;
        this.certificadoRepo = certificadoRepo;
    }

    @GetMapping
    public String dashboard(Model model) {
        long cursosActivos = cursoRepo.findAll().stream()
                .filter(c -> c.getEstado() == EstadoCurso.PUBLICADO || c.getEstado() == EstadoCurso.EN_CURSO)
                .count();
        long pagosPendientes = pagoRepo.countByEstado(EstadoPago.PENDIENTE);
        long inscripcionesAprobadas = inscripcionRepo.countByEstado(EstadoInscripcion.APROBADA);
        long certificadosEmitidos = certificadoRepo.count();

        model.addAttribute("cursosActivos", cursosActivos);
        model.addAttribute("pagosPendientes", pagosPendientes);
        model.addAttribute("inscripcionesAprobadas", inscripcionesAprobadas);
        model.addAttribute("certificadosEmitidos", certificadosEmitidos);
        model.addAttribute("ultimosPagosPendientes", pagoRepo.findByEstado(EstadoPago.PENDIENTE));

        return "admin/dashboard";
    }
}
