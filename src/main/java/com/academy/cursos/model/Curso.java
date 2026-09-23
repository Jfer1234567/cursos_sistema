package com.academy.cursos.model;

import com.academy.cursos.model.enums.EstadoCurso;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "cursos")
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(name = "descripcion_corta")
    private String descripcionCorta;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_investigacion_id", nullable = false)
    private AreaInvestigacion areaInvestigacion;

    private String duracion;

    @Column(name = "docente_responsable")
    private String docenteResponsable;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(name = "cupos_totales", nullable = false)
    private Integer cuposTotales;

    @Column(name = "cupos_disponibles", nullable = false)
    private Integer cuposDisponibles;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoCurso estado;

    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    @Column(name = "imagen_url")
    private String imagenUrl;

    @Column(name = "enlace_clase")
    private String enlaceClase;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (cuposDisponibles == null) {
            cuposDisponibles = cuposTotales;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcionCorta() { return descripcionCorta; }
    public void setDescripcionCorta(String descripcionCorta) { this.descripcionCorta = descripcionCorta; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public AreaInvestigacion getAreaInvestigacion() { return areaInvestigacion; }
    public void setAreaInvestigacion(AreaInvestigacion areaInvestigacion) { this.areaInvestigacion = areaInvestigacion; }
    public String getDuracion() { return duracion; }
    public void setDuracion(String duracion) { this.duracion = duracion; }
    public String getDocenteResponsable() { return docenteResponsable; }
    public void setDocenteResponsable(String docenteResponsable) { this.docenteResponsable = docenteResponsable; }
    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }
    public Integer getCuposTotales() { return cuposTotales; }
    public void setCuposTotales(Integer cuposTotales) { this.cuposTotales = cuposTotales; }
    public Integer getCuposDisponibles() { return cuposDisponibles; }
    public void setCuposDisponibles(Integer cuposDisponibles) { this.cuposDisponibles = cuposDisponibles; }
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
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
