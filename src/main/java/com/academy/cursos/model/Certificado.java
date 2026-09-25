package com.academy.cursos.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "certificados")
public class Certificado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inscripcion_id", nullable = false, unique = true)
    private Inscripcion inscripcion;

    @Column(name = "codigo_verificacion", nullable = false, unique = true, length = 50)
    private String codigoVerificacion;

    @Column(name = "fecha_emision", nullable = false)
    private LocalDate fechaEmision;

    @Column(name = "nombre_participante", nullable = false)
    private String nombreParticipante;

    @Column(name = "nombre_curso", nullable = false)
    private String nombreCurso;

    @Column(name = "linea_investigacion", nullable = false)
    private String lineaInvestigacion;

    @Column(name = "area_investigacion", nullable = false)
    private String areaInvestigacion;

    @Column(nullable = false)
    private String docente;

    @Column(nullable = false)
    private String duracion;

    @Column(name = "creditos")
    private Integer creditos = 2;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Inscripcion getInscripcion() { return inscripcion; }
    public void setInscripcion(Inscripcion inscripcion) { this.inscripcion = inscripcion; }
    public String getCodigoVerificacion() { return codigoVerificacion; }
    public void setCodigoVerificacion(String codigoVerificacion) { this.codigoVerificacion = codigoVerificacion; }
    public LocalDate getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(LocalDate fechaEmision) { this.fechaEmision = fechaEmision; }
    public String getNombreParticipante() { return nombreParticipante; }
    public void setNombreParticipante(String nombreParticipante) { this.nombreParticipante = nombreParticipante; }
    public String getNombreCurso() { return nombreCurso; }
    public void setNombreCurso(String nombreCurso) { this.nombreCurso = nombreCurso; }
    public String getLineaInvestigacion() { return lineaInvestigacion; }
    public void setLineaInvestigacion(String lineaInvestigacion) { this.lineaInvestigacion = lineaInvestigacion; }
    public String getAreaInvestigacion() { return areaInvestigacion; }
    public void setAreaInvestigacion(String areaInvestigacion) { this.areaInvestigacion = areaInvestigacion; }
    public String getDocente() { return docente; }
    public void setDocente(String docente) { this.docente = docente; }
    public String getDuracion() { return duracion; }
    public void setDuracion(String duracion) { this.duracion = duracion; }
    public Integer getCreditos() { return creditos; }
    public void setCreditos(Integer creditos) { this.creditos = creditos; }
}
