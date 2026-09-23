package com.academy.cursos.controller.admin;

import com.academy.cursos.model.ConfigPago;
import com.academy.cursos.service.ConfigPagoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/config")
public class AdminConfigController {

    private final ConfigPagoService configPagoService;

    public AdminConfigController(ConfigPagoService configPagoService) {
        this.configPagoService = configPagoService;
    }

    @GetMapping("/pago")
    public String verConfigPago(Model model) {
        ConfigPago config = configPagoService.obtenerConfigActiva();
        model.addAttribute("configPago", config);
        return "admin/config/pago";
    }

    @PostMapping("/pago")
    public String guardarConfigPago(
            @RequestParam String numeroYape,
            @RequestParam String titularYape,
            @RequestParam String instrucciones,
            @RequestParam(value = "qrFile", required = false) MultipartFile qrFile,
            RedirectAttributes redirectAttributes) {

        try {
            configPagoService.actualizarConfig(numeroYape, titularYape, instrucciones, qrFile);
            redirectAttributes.addFlashAttribute("successMsg", "Datos de pago de Yape actualizados correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Error al guardar configuración: " + e.getMessage());
        }

        return "redirect:/admin/config/pago";
    }
}
