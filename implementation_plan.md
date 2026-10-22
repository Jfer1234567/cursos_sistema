# Plan de Corrección: Dimensiones del Flyer y Eliminación de Fondo

## 1. Justificación y Objetivos
1. **Eliminar Marca de Agua del Logo:**
   - Remover completamente las imágenes `.destacado-bg-watermark` y sus estilos en `pages.css`, dejando el fondo blanco puro y limpio (`#FFFFFF`).
2. **Dimensionar el Flyer al Tamaño Completo del Recuadro Púrpura:**
   - La imagen en el contenedor se veía reducida porque era horizontal y el contenedor estaba centrado con altura automática (`align-items: center`).
   - Aplicar `align-items: stretch;` en `.destacado-editorial-grid` y `height: 100%; min-height: 520px;` en `.destacado-editorial-visual`.
   - Ajustar el grid a `1fr 1.35fr` para que ocupe todo el ancho derecho.
   - Configurar la imagen con `width: 100%; height: 100%; object-fit: cover;` para que llene el 100% del recuadro púrpura delimitado por el usuario, tal como en la maqueta original.

## 2. Archivos a Modificar
1. **`src/main/resources/templates/public/index.html`:**
   - Eliminar las etiquetas `<img>` con clase `.destacado-bg-watermark`.
2. **`src/main/resources/static/css/pages.css`:**
   - Eliminar reglas de `.destacado-bg-watermark`.
   - Ajustar `.destacado-editorial-grid` a `grid-template-columns: 1fr 1.35fr; align-items: stretch;`.
   - En `.destacado-editorial-visual`, fijar `height: 100%; min-height: 520px; display: flex;`.
   - En `.destacado-editorial-img`, configurar `width: 100%; height: 100%; min-height: 500px; object-fit: cover;`.
3. **`src/main/resources/templates/layout/main.html`:**
   - Incrementar versión de caché a `(v=7)`.

## 3. Plan de Verificación
1. Ejecutar `./gradlew test` (31/31 exitosos).
2. Desplegar a GitHub `main` para Render.
