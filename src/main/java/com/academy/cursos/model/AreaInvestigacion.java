package com.academy.cursos.model;

import jakarta.persistence.*;

@Entity
@Table(name = "areas_investigacion")
public class AreaInvestigacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "linea_investigacion_id", nullable = false)
    private LineaInvestigacion lineaInvestigacion;

    @Column(nullable = false)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LineaInvestigacion getLineaInvestigacion() { return lineaInvestigacion; }
    public void setLineaInvestigacion(LineaInvestigacion lineaInvestigacion) { this.lineaInvestigacion = lineaInvestigacion; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
