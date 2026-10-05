package com.academy.cursos.dto;

import com.academy.cursos.model.Curso;
import com.academy.cursos.model.enums.EstadoCurso;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CursoDTO {

    private Long id;

    @NotBlank(message = "El nombre del curso es obligatorio")
    private String nombre;

    private String descripcionCorta;

    @NotBlank(message = "La descripción detallada es obligatoria")
    private String descripcion;

    @NotNull(message = "Debe seleccionar un área de investigación")
    private Long areaInvestigacionId;

    @NotBlank(message = "La duración es obligatoria (ej: 40 horas académicas)")
    private String duracion;

    @NotBlank(message = "El docente responsable es obligatorio")
    private String docenteResponsable;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "El precio no puede ser negativo")
    private BigDecimal precio;

    @NotNull(message = "Los cupos totales son obligatorios")
    @Min(value = 1, message = "Debe haber al menos 1 cupo")
    private Integer cuposTotales;

    private EstadoCurso estado = EstadoCurso.BORRADOR;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaInicio;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaFin;

    private String imagenUrl;

    private String enlaceClase;

    private String enlaceWhatsapp;

    @DecimalMin(value = "0.0", inclusive = true, message = "El precio de comunidad no puede ser negativo")
    private BigDecimal precioComunidad;

    @NotNull(message = "El número de créditos es obligatorio")
    @Min(value = 1, message = "Debe otorgar al menos 1 crédito")
    private Integer creditos = 2;

    private String docenteFotoUrl;

    private String docenteCargo;

    private org.springframework.web.multipart.MultipartFile docenteFotoFile;

    private String flyerUrl;

    private Boolean flyerPublicado = false;

    private org.springframework.web.multipart.MultipartFile flyerFile;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcionCorta() { return descripcionCorta; }
    public void setDescripcionCorta(String descripcionCorta) { this.descripcionCorta = descripcionCorta; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public Long getAreaInvestigacionId() { return areaInvestigacionId; }
    public void setAreaInvestigacionId(Long areaInvestigacionId) { this.areaInvestigacionId = areaInvestigacionId; }
    public String getDuracion() { return duracion; }
    public void setDuracion(String duracion) { this.duracion = duracion; }
    public String getDocenteResponsable() { return docenteResponsable; }
    public void setDocenteResponsable(String docenteResponsable) { this.docenteResponsable = docenteResponsable; }
    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }
    public Integer getCuposTotales() { return cuposTotales; }
    public void setCuposTotales(Integer cuposTotales) { this.cuposTotales = cuposTotales; }
    public EstadoCurso getEstado() { return estado; }
    public void setEstado(EstadoCurso estado) { this.estado = estado; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }
    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }
    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
    public String getEnlaceClase() { return enlaceClase; }
    public void setEnlaceClase(String enlaceClase) { this.enlaceClase = enlaceClase; }
    public String getEnlaceWhatsapp() { return enlaceWhatsapp; }
    public void setEnlaceWhatsapp(String enlaceWhatsapp) { this.enlaceWhatsapp = enlaceWhatsapp; }
    public BigDecimal getPrecioComunidad() { return precioComunidad; }
    public void setPrecioComunidad(BigDecimal precioComunidad) { this.precioComunidad = precioComunidad; }
    public Integer getCreditos() { return creditos; }
    public void setCreditos(Integer creditos) { this.creditos = creditos; }
    public String getDocenteFotoUrl() { return docenteFotoUrl; }
    public void setDocenteFotoUrl(String docenteFotoUrl) { this.docenteFotoUrl = docenteFotoUrl; }
    public String getDocenteCargo() { return docenteCargo; }
    public void setDocenteCargo(String docenteCargo) { this.docenteCargo = docenteCargo; }
    public org.springframework.web.multipart.MultipartFile getDocenteFotoFile() { return docenteFotoFile; }
    public void setDocenteFotoFile(org.springframework.web.multipart.MultipartFile docenteFotoFile) { this.docenteFotoFile = docenteFotoFile; }

    public String getFlyerUrl() { return flyerUrl; }
    public void setFlyerUrl(String flyerUrl) { this.flyerUrl = flyerUrl; }
    public Boolean getFlyerPublicado() { return flyerPublicado != null && flyerPublicado; }
    public void setFlyerPublicado(Boolean flyerPublicado) { this.flyerPublicado = flyerPublicado; }
    public org.springframework.web.multipart.MultipartFile getFlyerFile() { return flyerFile; }
    public void setFlyerFile(org.springframework.web.multipart.MultipartFile flyerFile) { this.flyerFile = flyerFile; }

    public static CursoDTO fromEntity(Curso curso) {
        if (curso == null) {
            return null;
        }
        CursoDTO dto = new CursoDTO();
        dto.setId(curso.getId());
        dto.setNombre(curso.getNombre());
        dto.setDescripcionCorta(curso.getDescripcionCorta());
        dto.setDescripcion(curso.getDescripcion());
        if (curso.getAreaInvestigacion() != null) {
            dto.setAreaInvestigacionId(curso.getAreaInvestigacion().getId());
        }
        dto.setDuracion(curso.getDuracion());
        dto.setDocenteResponsable(curso.getDocenteResponsable());
        dto.setPrecio(curso.getPrecio());
        dto.setCuposTotales(curso.getCuposTotales());
        dto.setEstado(curso.getEstado());
        dto.setFechaInicio(curso.getFechaInicio());
        dto.setFechaFin(curso.getFechaFin());
        dto.setImagenUrl(curso.getImagenUrl());
        dto.setEnlaceClase(curso.getEnlaceClase());
        dto.setEnlaceWhatsapp(curso.getEnlaceWhatsapp());
        dto.setPrecioComunidad(curso.getPrecioComunidad());
        dto.setCreditos(curso.getCreditos());
        dto.setDocenteCargo(curso.getDocenteCargo());
        dto.setDocenteFotoUrl(curso.getDocenteFotoUrl());
        dto.setFlyerUrl(curso.getFlyerUrl());
        dto.setFlyerPublicado(curso.getFlyerPublicado());
        return dto;
    }
}
