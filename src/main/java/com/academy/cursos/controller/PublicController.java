package com.academy.cursos.controller;

import com.academy.cursos.dto.ContactoDTO;
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
