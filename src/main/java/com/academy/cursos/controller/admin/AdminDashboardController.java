package com.academy.cursos.controller.admin;

import com.academy.cursos.model.enums.EstadoCurso;
import com.academy.cursos.model.enums.EstadoInscripcion;
import com.academy.cursos.model.enums.EstadoPago;
import com.academy.cursos.model.enums.Rol;
import com.academy.cursos.repository.CertificadoRepository;
import com.academy.cursos.repository.CursoRepository;
import com.academy.cursos.repository.InscripcionRepository;
import com.academy.cursos.repository.MensajeContactoRepository;
import com.academy.cursos.repository.PagoRepository;
import com.academy.cursos.repository.UsuarioRepository;
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
    private final UsuarioRepository usuarioRepo;
    private final MensajeContactoRepository mensajeRepo;

    public AdminDashboardController(
            CursoRepository cursoRepo,
            InscripcionRepository inscripcionRepo,
            PagoRepository pagoRepo,
            CertificadoRepository certificadoRepo,
            UsuarioRepository usuarioRepo,
            MensajeContactoRepository mensajeRepo) {
        this.cursoRepo = cursoRepo;
        this.inscripcionRepo = inscripcionRepo;
        this.pagoRepo = pagoRepo;
        this.certificadoRepo = certificadoRepo;
        this.usuarioRepo = usuarioRepo;
        this.mensajeRepo = mensajeRepo;
    }

    @GetMapping
    public String dashboard(Model model) {
        long cursosActivos = cursoRepo.findAll().stream()
                .filter(c -> c.getEstado() == EstadoCurso.PUBLICADO || c.getEstado() == EstadoCurso.EN_CURSO)
                .count();
        long pagosPendientes = pagoRepo.countByEstado(EstadoPago.PENDIENTE);
        long inscripcionesAprobadas = inscripcionRepo.countByEstado(EstadoInscripcion.APROBADA);
        long certificadosEmitidos = certificadoRepo.count();
        long totalUsuarios = usuarioRepo.countByRol(Rol.PARTICIPANTE);
        long mensajesNoLeidos = mensajeRepo.countByLeidoFalse();

        model.addAttribute("cursosActivos", cursosActivos);
        model.addAttribute("pagosPendientes", pagosPendientes);
        model.addAttribute("inscripcionesAprobadas", inscripcionesAprobadas);
        model.addAttribute("certificadosEmitidos", certificadosEmitidos);
        model.addAttribute("totalUsuarios", totalUsuarios);
        model.addAttribute("mensajesNoLeidos", mensajesNoLeidos);
        model.addAttribute("ultimosPagosPendientes", pagoRepo.findByEstado(EstadoPago.PENDIENTE));

        return "admin/dashboard";
    }
}
