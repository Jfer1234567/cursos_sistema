# Plan de Corrección: Eliminación de Botón y Ajuste Visual del Flyer

## 1. Justificación y Objetivos
1. **Eliminar el Botón "Ver programa académico":**
   - Retirar el enlace secundario `[Ver programa académico]` en `src/main/resources/templates/public/index.html` para dejar únicamente el botón primario de inscripción `Inscribirme al curso →`, haciendo el llamado a la acción más directo.
2. **Corregir Visualización del Flyer (Evitar Recorte con `object-fit: contain`):**
   - El uso de `object-fit: cover` causó que la imagen del flyer se recortara lateralmente y superiormente.
   - Cambiar a `object-fit: contain; width: 100%; height: auto; max-height: 650px;` para que el afiche se muestre completo al 100% sin recortar texto ni encabezados, aprovechando al máximo el ancho del contenedor.
   - Restablecer `align-items: center;` en el grid para que el marco envuelva con elegancia el afiche sin forzar alturas artificiales.
3. **Corregir Ensanchamiento del Badge:**
   - Añadir `align-self: flex-start; width: fit-content;` a `.destacado-badge-pill` para evitar que se estire a lo largo de toda la columna.

## 2. Archivos a Modificar
1. **`src/main/resources/templates/public/index.html`:**
   - Eliminar el bloque `<a class="destacado-btn-secondary">...</a>`.
2. **`src/main/resources/static/css/pages.css`:**
   - Ajustar `.destacado-badge-pill` con `align-self: flex-start; width: fit-content;`.
   - Ajustar `.destacado-editorial-grid` a `align-items: center;`.
   - Ajustar `.destacado-editorial-visual` y `.destacado-editorial-img` para que el flyer escale al máximo con `object-fit: contain; height: auto; max-height: 650px;` sin recortarse.
3. **`src/main/resources/templates/layout/main.html`:**
   - Incrementar versión de caché a `(v=8)`.

## 3. Plan de Verificación
1. Ejecutar `./gradlew test` (validar 31/31 pruebas pasando).
2. Desplegar a GitHub `main` para Render.
