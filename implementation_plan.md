# Plan de Implementación: Fluidez Instantánea SPA y Optimización Extrema de Rendimiento

## 1. Diagnóstico del Problema ("Demora al dar clic a cualquier botón o función")

Aunque la compresión GZIP y el prefetch mejoraron la transferencia de recursos estáticos, la navegación y las funciones siguen sintiéndose lentas por 4 causas de fondo:

1. **Recarga Tradicional Completa del Navegador (Falta de Motor SPA / Pjax):**
   - Cada clic en un enlace o botón (`/catalogo`, `/cursos/{id}`, `/lineas-investigacion`, `/login`, etc.) destruye todo el DOM actual, deja la pantalla en blanco y espera a que el servidor envíe el nuevo HTML para reconstruir estilos, fuentes y scripts.
   - Las grandes plataformas (GitHub, Linear, Stripe, Shopify) **nunca** recargan toda la página; usan un motor de transición instantánea que intercambia el contenido en < 10 ms manteniendo estilos y scripts en memoria.

2. **Tormenta de Consultas SQL N+1 a Neon DB (Ohio):**
   - El servidor de Render está en una región y la base de datos Neon PostgreSQL está en Ohio (`us-east-2`). La latencia de ida y vuelta de red es de ~70 ms por consulta.
   - Varias vistas ejecutaban consultas perezosas (Lazy) dentro de bucles Thymeleaf:
     - En el catálogo y cursos destacados: cada curso consultaba por separado su `AreaInvestigacion` y su `LineaInvestigacion` (hasta 20 consultas individuales = 1.4 segundos de espera).
     - En el Dashboard de Administración: cada pago pendiente disparaba 3 consultas adicionales (`inscripcion`, `usuario`, `curso`). 5 pagos = 15 consultas = 1 segundo de espera.
     - En la bandeja de pagos pendientes (`/admin/inscripciones/pendientes`): 5 consultas por fila (hasta 3.5 segundos de bloqueo).
     - En la pestaña de usuarios (`/admin/usuarios`): un bucle `stream().map()` ejecutaba `countByCursoId` para cada curso uno a uno.

3. **Falta de Caché de Datos en Memoria RAM del Servidor:**
   - La información pública (líneas de investigación, áreas, cursos destacados, configuración de pagos) se consultaba una y otra vez en Neon DB en cada petición, cuando podría responderse en 0 ms desde la memoria RAM de Spring Boot.

4. **Tamaño Excesivo de Fotos de Vouchers (3MB a 8MB) y Subida Doble:**
   - Los participantes suben fotos de vouchers tomadas con smartphones de alta resolución (5MB).
   - Enviar 5MB por red móvil y luego que Render lo suba a Cloudinary tomaba 8-15 segundos.

---

## 2. Propuesta de Solución Integral

### A. Eliminación Radical de Consultas N+1 con `JOIN FETCH`
- **`CursoRepository.java`**:
  - `findTop6ByEstadoOrderByCreatedAtDesc` y `findByEstadoAndLineaInvestigacion` ahora usarán `LEFT JOIN FETCH c.areaInvestigacion a LEFT JOIN FETCH a.lineaInvestigacion` para traer todo el grafo en **1 sola consulta**.
- **`PagoRepository.java`**:
  - Nuevo método `findByEstadoConDetalles` con `JOIN FETCH p.inscripcion i JOIN FETCH i.usuario JOIN FETCH i.curso` para el Dashboard de Administración (1 consulta en lugar de 15).
- **`InscripcionRepository.java`**:
  - Nuevo método `findByEstadoConDetalles` con `JOIN FETCH i.usuario JOIN FETCH i.curso c JOIN FETCH c.areaInvestigacion a JOIN FETCH a.lineaInvestigacion LEFT JOIN FETCH i.pago` para la bandeja de verificación (1 consulta en lugar de 50).
  - Consulta agrupada `obtenerConteoAlumnosPorCurso()` que reemplaza el bucle N+1 en `AdminUsuarioController`.
- **`InscripcionService.java`**:
  - `listarPorUsuario` usará `findByUsuarioIdConCurso` para que el panel del estudiante cargue en 1 sola consulta.

### B. Caché de Alto Rendimiento en Memoria RAM (`@EnableCaching`)
- Activar `@EnableCaching` en `CursosApplication.java`.
- Cachear en `CursoService` los métodos `listarDestacados()`, `listarPublicados()` y `obtenerFlyerPrincipal()`.
- Cachear en `ConfigPagoService` la configuración de pago (`obtenerConfiguracion()`).
- Invalidación atómica (`@CacheEvict`) al crear, editar o cambiar estado de cursos o pagos para garantizar cero datos obsoletos.
- **Resultado:** El servidor responderá a los clics de navegación en **< 15 ms** en lugar de 400 ms.

### C. Motor de Navegación Instantánea SPA (Pjax / In-Memory Swapper) en `main.js`
- Al pasar el cursor o hacer touch sobre cualquier enlace interno, la página HTML se descarga y se guarda en un mapa en memoria (`pageCache`).
- Al hacer clic:
  - Si ya está en caché: se intercambia el contenido (`<main>` / `<body>`) y se actualiza el título en **0 a 5 milisegundos**.
  - Si aún no está en caché: se muestra la barra superior de progreso institucional inmediatamente (0 ms) y se hace un `fetch` suave sin recarga de pantalla en blanco.
  - Se actualiza la URL con `history.pushState` y se maneja `popstate` para que los botones Atrás/Adelante del navegador funcionen al instante.
  - Se re-inicializan componentes dinámicos (alertas, menús, vistas previas).

### D. Micro-Interacciones Táctiles y Feedback Inmediato en Botones
- Añadir en `components.css` la pseudo-clase `:active` (`transform: scale(0.97)`) para que cada botón y tarjeta reaccione al tacto en 0 ms.
- En formularios, al hacer clic en enviar:
  - Cambiar el botón inmediatamente a estado de carga con un spinner fino y texto activo ("Procesando...", "Subiendo comprobante...").
  - Deshabilitar el botón para evitar doble clic o peticiones duplicadas.

### E. Compresión Automática de Vouchers en el Navegador
- En `main.js`, detectar la selección de imágenes en el input de comprobantes.
- Comprimir la foto en 50 ms usando HTML5 Canvas (resolución máxima 1600px, calidad 0.85 JPEG/WebP) reduciendo el tamaño de 5MB a ~250KB sin pérdida de legibilidad de texto ni números de operación.
- La subida pasará de demorar 10 segundos a **menos de 0.5 segundos**.

---

## 3. Archivos a Modificar

1. `src/main/java/com/academy/cursos/CursosApplication.java` (añadir `@EnableCaching`).
2. `src/main/java/com/academy/cursos/repository/CursoRepository.java` (`JOIN FETCH` en cursos publicados y destacados).
3. `src/main/java/com/academy/cursos/repository/PagoRepository.java` (`JOIN FETCH` para pagos con detalles).
4. `src/main/java/com/academy/cursos/repository/InscripcionRepository.java` (`JOIN FETCH` para bandeja de inscripciones y consulta agrupada).
5. `src/main/java/com/academy/cursos/service/CursoService.java` (`@Cacheable` y `@CacheEvict`).
6. `src/main/java/com/academy/cursos/service/InscripcionService.java` (usar métodos optimizados con fetch).
7. `src/main/java/com/academy/cursos/service/ConfigPagoService.java` (`@Cacheable` y `@CacheEvict`).
8. `src/main/java/com/academy/cursos/controller/admin/AdminDashboardController.java` (usar `findByEstadoConDetalles`).
9. `src/main/java/com/academy/cursos/controller/admin/AdminInscripcionController.java` (usar `findByEstadoConDetalles`).
10. `src/main/java/com/academy/cursos/controller/admin/AdminUsuarioController.java` (usar conteo agrupado sin bucles).
11. `src/main/resources/static/js/main.js` (motor SPA Instant Click + compresión de imágenes + micro-feedback).
12. `src/main/resources/static/css/components.css` (estilos táctiles `:active`, spinners de botones).

---

## 4. Plan de Verificación

1. **Pruebas Unitarias y de Integración:**
   - Ejecutar `./gradlew test` asegurando que los 30 tests existentes pasen al 100%.
2. **Validación de Consultas SQL:**
   - Verificar que no ocurran `LazyInitializationException` y que no haya consultas duplicadas.
3. **Validación de Compresión Frontend:**
   - Comprobar que los scripts de `main.js` no tengan errores de sintaxis y que la navegación SPA funcione sin romper formularios ni descargas.
4. **Despliegue y Validación Cloud:**
   - Sincronizar en GitHub para que Render actualice el servicio de producción.
