package com.academy.cursos.controller;

import com.academy.cursos.dto.ContactoDTO;
import com.academy.cursos.model.Curso;
import com.academy.cursos.repository.AreaInvestigacionRepository;
import com.academy.cursos.repository.LineaInvestigacionRepository;
import com.academy.cursos.service.CursoService;
import com.academy.cursos.service.MensajeContactoService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class PublicController {

    private final LineaInvestigacionRepository lineaRepo;
    private final AreaInvestigacionRepository areaRepo;
    private final CursoService cursoService;
    private final MensajeContactoService mensajeContactoService;

    public PublicController(
            LineaInvestigacionRepository lineaRepo,
            AreaInvestigacionRepository areaRepo,
            CursoService cursoService,
            MensajeContactoService mensajeContactoService) {
        this.lineaRepo = lineaRepo;
        this.areaRepo = areaRepo;
        this.cursoService = cursoService;
        this.mensajeContactoService = mensajeContactoService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("lineas", lineaRepo.findAllByOrderByOrdenAsc());

        Optional<Curso> flyerOpt = cursoService.obtenerFlyerPrincipal();
        Curso flyerDestacado = flyerOpt.orElse(null);
        model.addAttribute("flyerDestacado", flyerDestacado);

        // Si hay un evento con flyer arriba, se excluye de la cuadrícula inferior para evitar duplicación.
        // Cuando se cree un nuevo evento, ese ocupará la parte superior y el anterior bajará a la lista automáticamente.
        List<Curso> cursos = cursoService.listarDestacados();
        if (flyerDestacado != null) {
            cursos = cursos.stream()
                    .filter(c -> !c.getId().equals(flyerDestacado.getId()))
                    .collect(Collectors.toList());
        }
        model.addAttribute("cursosDestacados", cursos);
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
    public String contacto(Model model) {
        if (!model.containsAttribute("contactoDTO")) {
            model.addAttribute("contactoDTO", new ContactoDTO());
        }
        return "public/contacto";
    }

    @PostMapping("/contacto")
    public String procesarContacto(
            @Valid @ModelAttribute("contactoDTO") ContactoDTO contactoDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "public/contacto";
        }

        mensajeContactoService.guardarMensaje(contactoDTO);
        redirectAttributes.addFlashAttribute("successMsg", 
            "¡Tu consulta ha sido enviada con éxito! El equipo de coordinación del IIICCD revisará tu mensaje y se comunicará contigo a la brevedad.");

        return "redirect:/contacto";
    }
}
