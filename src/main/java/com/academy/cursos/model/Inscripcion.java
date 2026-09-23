package com.academy.cursos.model;

import com.academy.cursos.model.enums.EstadoInscripcion;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "inscripciones")
public class Inscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curso_id", nullable = false)
    private Curso curso;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoInscripcion estado;

    @Column(name = "fecha_inscripcion", nullable = false)
    private LocalDateTime fechaInscripcion;

    @Column(name = "notas_admin", columnDefinition = "TEXT")
    private String notasAdmin;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "inscripcion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Pago pago;

    @OneToOne(mappedBy = "inscripcion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Certificado certificado;

    @PrePersist
    protected void onCreate() {
        if (fechaInscripcion == null) {
            fechaInscripcion = LocalDateTime.now();
        }
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public Curso getCurso() { return curso; }
    public void setCurso(Curso curso) { this.curso = curso; }
    public EstadoInscripcion getEstado() { return estado; }
    public void setEstado(EstadoInscripcion estado) { this.estado = estado; }
    public LocalDateTime getFechaInscripcion() { return fechaInscripcion; }
    public void setFechaInscripcion(LocalDateTime fechaInscripcion) { this.fechaInscripcion = fechaInscripcion; }
    public String getNotasAdmin() { return notasAdmin; }
    public void setNotasAdmin(String notasAdmin) { this.notasAdmin = notasAdmin; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public Pago getPago() { return pago; }
    public void setPago(Pago pago) { this.pago = pago; }
    public Certificado getCertificado() { return certificado; }
    public void setCertificado(Certificado certificado) { this.certificado = certificado; }
}
