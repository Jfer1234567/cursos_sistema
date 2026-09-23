package com.academy.cursos.model;

import com.academy.cursos.model.enums.EstadoPago;
import com.academy.cursos.model.enums.MetodoPago;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagos")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inscripcion_id", nullable = false)
    private Inscripcion inscripcion;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago", nullable = false)
    private MetodoPago metodoPago;

    @Column(name = "referencia_externa")
    private String referenciaExterna;

    @Column(name = "comprobante_url")
    private String comprobanteUrl;

    @Column(name = "comprobante_nombre_original")
    private String comprobanteNombreOriginal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPago estado;

    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;

    @Column(name = "fecha_verificacion")
    private LocalDateTime fechaVerificacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verificado_por")
    private Usuario verificadoPor;

    @Column(columnDefinition = "TEXT")
    private String notas;

    @PrePersist
    protected void onCreate() {
        if (fechaPago == null) {
            fechaPago = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Inscripcion getInscripcion() { return inscripcion; }
    public void setInscripcion(Inscripcion inscripcion) { this.inscripcion = inscripcion; }
    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }
    public MetodoPago getMetodoPago() { return metodoPago; }
    public void setMetodoPago(MetodoPago metodoPago) { this.metodoPago = metodoPago; }
    public String getReferenciaExterna() { return referenciaExterna; }
    public void setReferenciaExterna(String referenciaExterna) { this.referenciaExterna = referenciaExterna; }
    public String getComprobanteUrl() { return comprobanteUrl; }
    public void setComprobanteUrl(String comprobanteUrl) { this.comprobanteUrl = comprobanteUrl; }
    public String getComprobanteNombreOriginal() { return comprobanteNombreOriginal; }
    public void setComprobanteNombreOriginal(String comprobanteNombreOriginal) { this.comprobanteNombreOriginal = comprobanteNombreOriginal; }
    public EstadoPago getEstado() { return estado; }
    public void setEstado(EstadoPago estado) { this.estado = estado; }
    public LocalDateTime getFechaPago() { return fechaPago; }
    public void setFechaPago(LocalDateTime fechaPago) { this.fechaPago = fechaPago; }
    public LocalDateTime getFechaVerificacion() { return fechaVerificacion; }
    public void setFechaVerificacion(LocalDateTime fechaVerificacion) { this.fechaVerificacion = fechaVerificacion; }
    public Usuario getVerificadoPor() { return verificadoPor; }
    public void setVerificadoPor(Usuario verificadoPor) { this.verificadoPor = verificadoPor; }
    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
}
