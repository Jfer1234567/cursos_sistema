package com.academy.cursos.dto;

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
}
