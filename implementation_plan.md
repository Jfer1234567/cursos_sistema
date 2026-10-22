# Plan de Implementación: Ampliación del Flyer y Marca de Agua del Logo Institucional

## 1. Justificación y Objetivos
Atender las dos solicitudes de refinamiento visual sobre la sección destacada de inicio:
1. **Ampliación del Flyer Oficial:**
   - Actualmente el afiche se percibe reducido debido a la proporción de columnas (`1.15fr 1fr`) y a un límite de altura de `520px`.
   - Ajustar la cuadrícula a `1fr 1.22fr` (o `1.05fr 1.25fr`) otorgando mayor espacio horizontal al afiche.
   - Incrementar `max-height` a `680px` en `.destacado-editorial-img` para que los afiches verticales/estándar se muestren grandes, nítidos e imponentes.
2. **Fondo Sutil con el Logo Institucional en la Sección:**
   - Integrar el isotipo institucional (`logo-isotipo-watermark.png`) como marca de agua de fondo translúcida (`opacity: 0.055 - 0.065`) en la sección blanca.
   - Posicionar la marca de agua de forma orgánica detrás del bloque de información (`.destacado-bg-watermark`), manteniendo legibilidad del 100% en los textos.

## 2. Archivos a Modificar
1. **`src/main/resources/templates/public/index.html`:**
   - Insertar la imagen de marca de agua decorativa `logo-isotipo-watermark.png` con clase `.destacado-bg-watermark` dentro de `.seccion-flyer-destacado`.
2. **`src/main/resources/static/css/pages.css`:**
   - Ajustar `.destacado-editorial-grid` para expandir el área del flyer (`grid-template-columns: 1.02fr 1.25fr; gap: 3rem;`).
   - Incrementar `.destacado-editorial-img` (`max-height: 680px;`).
   - Crear la regla `.destacado-bg-watermark` con posición absoluta, opacidad calibrada (`0.055`), rotación sutil y sin eventos de puntero.
   - Asegurar `overflow: hidden; position: relative;` en `.seccion-flyer-destacado`.
   - Garantizar `position: relative; z-index: 2;` en el contenido y visual.
3. **`src/main/resources/templates/layout/main.html`:**
   - Incrementar la versión de caché a `(v=6)`.

## 3. Plan de Verificación
1. Ejecutar `./gradlew test` (validar 31/31 tests pasando).
2. Verificar responsividad y límites en resoluciones móviles y pantallas de escritorio.
3. Desplegar a GitHub `main` para actualización en Render.
