# Plan de Implementación: Rediseño Editorial Limpio del Curso Destacado en Inicio

## 1. Justificación y Objetivos de Diseño
Reemplazar el diseño de la sección de afiche destacado (anteriormente oscuro/cyber) por una tarjeta editorial moderna con fondo claro y acabados institucionales de alta gama, inspirada en la maqueta compartida por el usuario:
- **Estructura Visual:**
  - Fondo blanco sobre un contenedor suave con borde sutil y sombras limpias.
  - Insignia suave: `● INSCRIPCIONES ABIERTAS`.
  - Título editorial en gran formato (`#0F172A`) con subtítulo y línea de acento dorada.
  - Párrafo descriptivo breve y legible.
  - Fila de metadatos académicos con íconos circulares discretos: *Docente*, *Fecha de Inicio*, *Duración (horas)* y *Créditos*.
  - Acciones: Botón primario granate `Inscribirme al curso →` y enlace secundario `Ver programa académico →`.
  - Tarifas: Tarjetas suaves diferenciadas para *Público General* (verde suave) y *Comunidad UNA Puno* (dorado/crema suave).
  - Lado derecho: Imagen del afiche / temática gráfica con `border-radius: 16px` y sombra suave.

## 2. Archivos a Modificar
1. **`src/main/resources/static/css/pages.css`:**
   - Nuevas clases de estilo para el bloque editorial destacado (`.seccion-curso-destacado-editorial`, `.destacado-editorial-grid`, `.destacado-badge-pill`, `.destacado-title`, `.destacado-sub`, `.destacado-meta-row`, `.destacado-meta-item`, `.destacado-pricing-row`, `.destacado-price-card`, `.destacado-img-wrap`).
   - Reglas responsivas para que en móviles y tablets se apile de forma limpia a una sola columna.
2. **`src/main/resources/templates/public/index.html`:**
   - Actualizar el fragmento Thymeleaf de `th:if="${flyerDestacado != null and ...}"` con el nuevo marcado editorial.
3. **`src/main/resources/templates/layout/main.html`:**
   - Actualizar versión `(v=5)` en `pages.css`, `main.css` y `components.css`.

## 3. Plan de Verificación
1. Ejecutar `./gradlew test` (validar 31/31 tests pasando).
2. Commit y push a GitHub `main` para Render.
