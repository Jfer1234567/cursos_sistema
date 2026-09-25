package com.academy.cursos.model.enums;

public enum EstadoInscripcion {
    PENDIENTE_PAGO("Pendiente de Pago"),
    PENDIENTE_VERIFICACION("Pendiente de Verificación"),
    APROBADA("Aprobada"),
    RECHAZADA("Rechazada"),
    COMPLETADA("Completada");

    private final String etiqueta;

    EstadoInscripcion(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
