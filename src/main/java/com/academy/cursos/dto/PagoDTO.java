package com.academy.cursos.dto;

import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PagoDTO {

    @NotNull(message = "El id de inscripción es obligatorio")
    private Long inscripcionId;

    @NotBlank(message = "El número de operación es obligatorio")
    private String referenciaExterna;

    private MultipartFile comprobante;

    public Long getInscripcionId() { return inscripcionId; }
    public void setInscripcionId(Long inscripcionId) { this.inscripcionId = inscripcionId; }
    public String getReferenciaExterna() { return referenciaExterna; }
    public void setReferenciaExterna(String referenciaExterna) { this.referenciaExterna = referenciaExterna; }
    public MultipartFile getComprobante() { return comprobante; }
    public void setComprobante(MultipartFile comprobante) { this.comprobante = comprobante; }
}
