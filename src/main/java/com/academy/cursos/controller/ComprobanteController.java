package com.academy.cursos.controller;

import com.academy.cursos.model.Pago;
import com.academy.cursos.model.Usuario;
import com.academy.cursos.repository.PagoRepository;
import com.academy.cursos.service.ArchivoService;
import com.academy.cursos.service.UsuarioService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

@Controller
public class ComprobanteController {

    private final ArchivoService archivoService;
    private final PagoRepository pagoRepository;
    private final UsuarioService usuarioService;

    public ComprobanteController(
            ArchivoService archivoService,
            PagoRepository pagoRepository,
            UsuarioService usuarioService) {
        this.archivoService = archivoService;
        this.pagoRepository = pagoRepository;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/comprobantes/{archivo}")
    public ResponseEntity<Resource> verComprobante(
            @PathVariable String archivo,
            @AuthenticationPrincipal UserDetails userDetails) throws IOException {

        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        Usuario usuario = usuarioService.buscarPorCorreo(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        String rutaRelativa = "comprobantes/" + archivo;
        Optional<Pago> pagoOpt = pagoRepository.findAll().stream()
                .filter(p -> rutaRelativa.equals(p.getComprobanteUrl()))
                .findFirst();

        if (pagoOpt.isPresent()) {
            Pago pago = pagoOpt.get();
            boolean esDuenio = pago.getInscripcion().getUsuario().getId().equals(usuario.getId());
            boolean esAdmin = usuario.getRol().name().equals("ADMIN");

            if (!esDuenio && !esAdmin) {
                return ResponseEntity.status(403).build();
            }
        }

        Resource recurso = archivoService.cargarComoRecurso(rutaRelativa);
        String contentType = Files.probeContentType(Paths.get(recurso.getFile().getAbsolutePath()));
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + recurso.getFilename() + "\"")
                .body(recurso);
    }
}
