# Plan de Implementación: Soporte Integral para Cursos Gratuitos (Acceso Libre) sin Yape

## 1. Justificación y Objetivos de Negocio
Permitir que el Instituto IIICCD ofrezca capacitaciones, talleres introductorios y webinars de **Acceso Libre y Gratuito (Precio: S/ 0.00)** sin fricción para los participantes:
1. **Omisión Absoluta del Paso de Pago:** Un curso con costo cero no debe solicitar transferencia por Yape, captura de pantalla de comprobante ni número de operación.
2. **Matrícula Inmediata (Zero-Click Payment):** Al hacer clic en «Inscribirme Gratis», el sistema aprueba inmediatamente la inscripción (`APROBADA`), descuenta el cupo disponible y le otorga acceso al enlace de Meet/Zoom y grupo de WhatsApp al instante.
3. **Claridad Visual:** En catálogo, detalle y portada, sustituir el texto `S/ 0.00` por insignias atractivas de **`GRATIS`** o **`ACCESO LIBRE`** y botones con texto específico: **`Inscribirme Gratis`**.

---

## 2. Cambios Técnicos Detallados

### A. Modelo de Dominio (`Curso.java`)
- Añadir método de conveniencia:
  ```java
  public boolean esGratuito() {
      return precio == null || precio.compareTo(BigDecimal.ZERO) <= 0;
  }
  ```

### B. Capa de Servicios (`InscripcionService.java`)
- En `iniciarInscripcion(Long usuarioId, Long cursoId)`:
  - Validar cupos disponibles > 0.
  - Si `curso.esGratuito()`:
    - Asignar estado directo: `inscripcion.setEstado(EstadoInscripcion.APROBADA);`
    - Descontar cupo: `curso.setCuposDisponibles(curso.getCuposDisponibles() - 1); cursoRepository.save(curso);`
    - Enviar correo transaccional de bienvenida y confirmación de acceso inmediato.
    - Persistir y retornar la inscripción aprobada.

### C. Controlador de Inscripción (`InscripcionController.java`)
- En `iniciarInscripcion`:
  - Si la inscripción resultante está en estado `APROBADA`, redirigir directamente a `/participante/dashboard` con mensaje flash de éxito:
    *«¡Inscripción exitosa! Tienes acceso inmediato al curso gratuito [Nombre]. Enlaces y materiales disponibles en tu panel.»*
- En `verFormularioPago`:
  - Si la inscripción ya está `APROBADA` o el curso es gratuito, redirigir a `/participante/dashboard` evitando que el usuario acceda a la vista de Yape.

### D. Vistas Frontend (Thymeleaf)
- **`public/catalogo.html`:**
  - Si `curso.esGratuito()` o `curso.precio.compareTo(T(java.math.BigDecimal).ZERO) <= 0`:
    - Mostrar badge verde: `<span class="badge badge-success" style="background: #059669; color: #FFFFFF; font-weight: 700;">GRATIS</span>`.
    - Botón de tarjeta: `Ver Detalles (Acceso Libre)`.
- **`public/curso-detalle.html`:**
  - Bloque de inversión: Reemplazar el monto en soles por caja verde destacada **`Curso 100% Gratuito - Acceso Libre`**.
  - Botón principal de llamada a la acción: `<button type="submit" class="btn btn-primary btn-block">Inscribirme Gratis (Acceso Inmediato)</button>`.
  - Nota informativa aclarando que no requiere pago ni comprobante.
- **`public/index.html`:**
  - En la sección de flyer destacado y próximos cursos, mostrar el badge de curso gratuito si corresponde.
- **`admin/cursos/lista.html`:**
  - Columna de precio: Si es 0, mostrar `<span class="badge badge-success">Gratis</span>` en vez de `S/ 0.00`.
- **`admin/cursos/form.html`:**
  - Texto de ayuda en campo precio: *"Ingresa 0.00 si deseas que el curso sea gratuito (los alumnos se inscribirán de forma automática y directa sin solicitar Yape)."*

---

## 3. Plan de Verificación
1. **Pruebas Automatizadas Unitarias:**
   - Crear test `testInscripcionCursoGratuitoAprobacionInmediata()` en `MejorasProfesionalesTest.java`:
     - Asignar `precio = 0.00` a un curso con cupos disponibles.
     - Invocar `iniciarInscripcion`.
     - Validar que el estado retornado sea `APROBADA` y los cupos hayan disminuido en 1.
2. **Suite Completa:**
   - Ejecutar `./gradlew test` asegurando que el 100% de las pruebas pasen sin regresiones.
3. **Despliegue y Validación:**
   - Sincronizar en GitHub `main` para Render y verificar en servidor local.
