package com.academy.cursos.controller.admin;

import com.academy.cursos.model.Usuario;
import com.academy.cursos.repository.InscripcionRepository;
import com.academy.cursos.service.UsuarioService;
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

    public AdminUsuarioController(UsuarioService usuarioService, InscripcionRepository inscripcionRepo) {
        this.usuarioService = usuarioService;
        this.inscripcionRepo = inscripcionRepo;
    }

    public static class UsuarioResumenItem {
        private final Usuario usuario;
        private final long totalInscripciones;

        public UsuarioResumenItem(Usuario usuario, long totalInscripciones) {
            this.usuario = usuario;
            this.totalInscripciones = totalInscripciones;
        }

        public Usuario getUsuario() {
            return usuario;
        }

        public long getTotalInscripciones() {
            return totalInscripciones;
        }
    }

    @GetMapping
    public String listarUsuarios(
            @RequestParam(required = false) String filtro,
            Model model) {

        List<Usuario> usuarios = usuarioService.listarParticipantes(filtro);
        List<UsuarioResumenItem> items = usuarios.stream()
                .map(u -> new UsuarioResumenItem(u, inscripcionRepo.countByUsuarioId(u.getId())))
                .collect(Collectors.toList());

        long totalRegistrados = usuarioService.contarParticipantes();

        model.addAttribute("items", items);
        model.addAttribute("totalRegistrados", totalRegistrados);
        model.addAttribute("filtro", filtro != null ? filtro.trim() : "");

        return "admin/usuarios/index";
    }
}
