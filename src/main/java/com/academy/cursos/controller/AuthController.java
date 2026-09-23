package com.academy.cursos.controller;

import com.academy.cursos.dto.RegistroDTO;
import com.academy.cursos.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/login")
    public String login(
            @RequestParam(required = false) String error,
            @RequestParam(required = false) String logout,
            Model model) {
        if (error != null) {
            model.addAttribute("errorMsg", "Correo o contraseña incorrectos, o la cuenta está inactiva.");
        }
        if (logout != null) {
            model.addAttribute("successMsg", "Has cerrado sesión correctamente.");
        }
        return "auth/login";
    }

    @GetMapping("/registro")
    public String formRegistro(Model model) {
        if (!model.containsAttribute("registroDTO")) {
            model.addAttribute("registroDTO", new RegistroDTO());
        }
        return "auth/registro";
    }

    @PostMapping("/registro")
    public String procesarRegistro(
            @Valid @ModelAttribute("registroDTO") RegistroDTO registroDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "auth/registro";
        }

        try {
            usuarioService.registrar(registroDTO);
            redirectAttributes.addFlashAttribute("successMsg", "¡Registro exitoso! Ya puedes iniciar sesión con tu cuenta.");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            bindingResult.rejectValue("correo", "error.registroDTO", e.getMessage());
            return "auth/registro";
        }
    }
}
