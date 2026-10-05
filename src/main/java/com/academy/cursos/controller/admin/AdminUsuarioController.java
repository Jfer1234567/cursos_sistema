package com.academy.cursos.controller.admin;

import com.academy.cursos.model.Inscripcion;
import com.academy.cursos.model.Usuario;
import com.academy.cursos.repository.CursoRepository;
import com.academy.cursos.repository.InscripcionRepository;
import com.academy.cursos.service.UsuarioService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/usuarios")
public class AdminUsuarioController {

    private final UsuarioService usuarioService;
    private final InscripcionRepository inscripcionRepo;
    private final CursoRepository cursoRepo;

    public AdminUsuarioController(UsuarioService usuarioService, InscripcionRepository inscripcionRepo, CursoRepository cursoRepo) {
        this.usuarioService = usuarioService;
        this.inscripcionRepo = inscripcionRepo;
        this.cursoRepo = cursoRepo;
    }

    public static class CursoTabItem {
        private final Long id;
        private final String nombre;
        private final long totalAlumnos;

        public CursoTabItem(Long id, String nombre, long totalAlumnos) {
            this.id = id;
            this.nombre = nombre;
            this.totalAlumnos = totalAlumnos;
        }

        public Long getId() {
            return id;
        }

        public String getNombre() {
            return nombre;
        }

        public long getTotalAlumnos() {
            return totalAlumnos;
        }
    }

    public static class UsuarioResumenItem {
        private final Usuario usuario;
        private final long totalInscripciones;
        private final List<Inscripcion> inscripciones;

        public UsuarioResumenItem(Usuario usuario, long totalInscripciones, List<Inscripcion> inscripciones) {
            this.usuario = usuario;
            this.totalInscripciones = totalInscripciones;
            this.inscripciones = inscripciones != null ? inscripciones : List.of();
        }

        public Usuario getUsuario() {
            return usuario;
        }

        public long getTotalInscripciones() {
            return totalInscripciones;
        }

        public List<Inscripcion> getInscripciones() {
            return inscripciones;
        }
    }

    @GetMapping
    public String listarUsuarios(
            @RequestParam(required = false) String filtro,
            @RequestParam(required = false) String cursoFiltro,
            @RequestParam(defaultValue = "todos") String tab,
            @RequestParam(required = false) Long cursoId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            Model model) {

        // Normalizar selección del desplegable vs parámetros tab/cursoId
        if (cursoFiltro != null && !cursoFiltro.trim().isEmpty()) {
            if ("sin-cursos".equalsIgnoreCase(cursoFiltro.trim())) {
                tab = "sin-cursos";
                cursoId = null;
            } else if (cursoFiltro.trim().matches("\\d+")) {
                tab = "curso";
                cursoId = Long.parseLong(cursoFiltro.trim());
            } else {
                tab = "todos";
                cursoId = null;
                cursoFiltro = "todos";
            }
        } else {
            if ("sin-cursos".equalsIgnoreCase(tab)) {
                cursoFiltro = "sin-cursos";
            } else if ("curso".equalsIgnoreCase(tab) && cursoId != null) {
                cursoFiltro = String.valueOf(cursoId);
            } else {
                tab = "todos";
                cursoId = null;
                cursoFiltro = "todos";
            }
        }

        int pageSize = 4;
        Page<Usuario> usuariosPage;

        if ("sin-cursos".equalsIgnoreCase(tab)) {
            usuariosPage = usuarioService.listarParticipantesSinCursosPaginado(filtro, page, pageSize);
        } else if ("curso".equalsIgnoreCase(tab) && cursoId != null) {
            usuariosPage = usuarioService.listarParticipantesPaginadoPorCurso(cursoId, filtro, page, pageSize);
        } else {
            tab = "todos";
            cursoId = null;
            usuariosPage = usuarioService.listarParticipantesPaginado(filtro, page, pageSize);
        }

        List<UsuarioResumenItem> items = usuariosPage.getContent().stream()
                .map(u -> {
                    List<Inscripcion> ins = inscripcionRepo.findByUsuarioIdConCurso(u.getId());
                    return new UsuarioResumenItem(u, ins.size(), ins);
                })
                .collect(Collectors.toList());

        long totalRegistrados = usuarioService.contarParticipantes();
        long totalSinCursos = usuarioService.contarSinInscripciones();

        List<CursoTabItem> cursosTabs = cursoRepo.findAll().stream()
                .map(c -> new CursoTabItem(c.getId(), c.getNombre(), inscripcionRepo.countByCursoId(c.getId())))
                .collect(Collectors.toList());

        final Long finalCursoId = cursoId;
        CursoTabItem cursoSeleccionado = (finalCursoId != null)
                ? cursosTabs.stream().filter(c -> c.getId().equals(finalCursoId)).findFirst().orElse(null)
                : null;

        model.addAttribute("items", items);
        model.addAttribute("usuariosPage", usuariosPage);
        model.addAttribute("totalRegistrados", totalRegistrados);
        model.addAttribute("totalSinCursos", totalSinCursos);
        model.addAttribute("cursosTabs", cursosTabs);
        model.addAttribute("cursoFiltro", cursoFiltro);
        model.addAttribute("cursoSeleccionado", cursoSeleccionado);
        model.addAttribute("tab", tab);
        model.addAttribute("cursoId", cursoId);
        model.addAttribute("filtro", filtro != null ? filtro.trim() : "");
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", usuariosPage.getTotalPages());
        model.addAttribute("totalElements", usuariosPage.getTotalElements());

        return "admin/usuarios/index";
    }
}
