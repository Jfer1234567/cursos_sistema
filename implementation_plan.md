# Plan de Implementación: Eliminación del Generador de Flyers Oficiales

## 1. Justificación y Alcance
El sistema cuenta con soporte para carga manual de flyers promocionales directamente en la creación/edición de cursos (`flyerFile` / `flyerUrl`), permitiendo a los administradores subir el arte final diseñado por el equipo de marketing.

Por lo tanto, la herramienta de generación interna automatizada de afiches mediante canvas HTML (`/admin/flyer`) resulta redundante e innecesaria. Se procederá a su retiro total para simplificar la arquitectura, reducir deuda técnica y eliminar código muerto.

## 2. Archivos Afectados

### Archivos a Eliminar (Código y Recursos Muertos)
1. `src/main/java/com/academy/cursos/controller/admin/AdminFlyerController.java`:
   - Controlador de los endpoints `/admin/flyer`, `/admin/flyer/curso/{id}` y `/admin/cursos/{id}/flyer`.
2. `src/test/java/com/academy/cursos/AdminFlyerControllerTest.java`:
   - Pruebas unitarias asociadas a las rutas del generador.
3. `src/main/resources/templates/admin/flyer/editor.html`:
   - Vista Thymeleaf del lienzo y panel de personalización de afiches.
4. `src/main/resources/static/js/flyer-generator.js`:
   - Script cliente que interactuaba con `html2canvas` para renderizar el afiche interactivo.

### Archivos a Modificar
1. `src/main/resources/templates/admin/cursos/lista.html`:
   - Remover del menú de acciones de cada curso la opción "Estudio de Flyer Oficial" (`/admin/flyer/curso/{id}`).
2. `src/main/resources/static/css/pages.css`:
   - Remover las clases del bloque `Estudio de Diseño de Flyers Institucionales (Flyer Studio)` (líneas 1261 a 1680).
3. `src/main/resources/static/js/main.js`:
   - Remover la exclusión innecesaria del interceptor SPA `if (url.pathname.includes('/admin/flyer')) return false;`.
4. `memory-bank/activeContext.md` y `memory-bank/progress.md`:
   - Actualizar bitácora del proyecto reflejando la simplificación hacia la carga manual de afiches oficiales.

## 3. Plan de Verificación
1. **Compilación y Pruebas Unitarias:**
   - Ejecutar `./gradlew test` verificando que todas las pruebas (en especial `CursoFlyerTest`, `PublicControllerTest`, etc.) pasen limpiamente sin dependencias rotas.
2. **Revisión de Integridad:**
   - Confirmar que la subida manual de flyers, el toggle de visibilidad en portada y el carrusel/banner de inicio sigan funcionando intactos.
3. **Despliegue:**
   - Realizar commit descriptivo y push a `main` para actualización en Render.
