package com.academy.cursos.model;

import jakarta.persistence.*;

@Entity
@Table(name = "config_pago")
public class ConfigPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_metodo", nullable = false)
    private String nombreMetodo = "Yape";

    @Column(name = "numero_yape")
    private String numeroYape;

    @Column(name = "titular_yape")
    private String titularYape;

    @Column(name = "qr_imagen_url")
    private String qrImagenUrl;

    @Column(columnDefinition = "TEXT")
    private String instrucciones;

    @Column(nullable = false)
    private Boolean activo = true;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombreMetodo() { return nombreMetodo; }
    public void setNombreMetodo(String nombreMetodo) { this.nombreMetodo = nombreMetodo; }
    public String getNumeroYape() { return numeroYape; }
    public void setNumeroYape(String numeroYape) { this.numeroYape = numeroYape; }
    public String getTitularYape() { return titularYape; }
    public void setTitularYape(String titularYape) { this.titularYape = titularYape; }
    public String getQrImagenUrl() { return qrImagenUrl; }
    public void setQrImagenUrl(String qrImagenUrl) { this.qrImagenUrl = qrImagenUrl; }
    public String getInstrucciones() { return instrucciones; }
    public void setInstrucciones(String instrucciones) { this.instrucciones = instrucciones; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}
