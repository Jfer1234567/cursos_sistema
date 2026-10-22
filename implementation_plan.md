# Plan de Implementación: Diseño Web Responsivo Integral (Responsive Web Design)

## 1. Justificación y Objetivos de Negocio
Adaptar la plataforma institucional del **IIICCD - FINESI / UNA Puno** para que brinde una experiencia visual, táctil y funcional de primer nivel en la totalidad de dispositivos del mercado:
- **Computadoras:** Monitores grandes (1440px, 1920px Full HD y 4K), medianos (1200px) y escritorios compactos.
- **Laptops:** Resoluciones estándar de estudio y trabajo (1366x768, 1280x800, 1024x768).
- **Tablets:** Orientación horizontal (1024x768) y vertical (768x1024, iPad / Samsung Galaxy Tab).
- **Celulares:** Dispositivos Android e iPhone en orientación vertical (360px, 375px, 390px, 412px, 430px) y horizontal (*landscape*).

---

## 2. Matriz de Breakpoints y Comportamiento Adaptativo

| Dispositivo / Categoría | Rango de Ancho | Comportamiento del Layout | Navegación |
| :--- | :--- | :--- | :--- |
| **Monitores Grandes** | &ge; 1440px | Contenedor centrado max `1280px`, tipografía espaciosa, grillas de 3 a 4 columnas. | Barra horizontal completa con botones destacados. |
| **Laptops y Escritorios Medianos** | 1025px - 1439px | Contenedor max `1200px`, padding lateral proporcional (`1.5rem`), grillas de 3 columnas. | Barra horizontal completa con espaciado equilibrado. |
| **Tablets en Horizontal** | 993px - 1024px | Grillas de 2 a 3 columnas, sidebars reducidos, cards con proporciones optimizadas. | Barra horizontal con espaciado compacto. |
| **Tablets en Vertical y Phablets** | 769px - 992px | Grillas de 2 columnas, tablas con scroll táctil acelerado, formularios apilables. | Botón hamburguesa con menú colapsable tipo drawer. |
| **Celulares Estándar** | 481px - 768px | Grillas apiladas a 1 o 2 columnas, botones táctiles de ancho completo (`touch targets` &ge; 44px). | Menú hamburguesa desplegable con overlay y cierre táctil. |
| **Celulares Pequeños (iPhone/Android)** | &le; 480px | Columna única (`1fr`), tipografía fluida con `clamp()`, badges compactos, campos a pantalla completa. | Cabecera minimalista: logo + siglas + botón menú animado a 'X'. |

---

## 3. Plan de Cambios Técnicos Detallados

### A. Tipografía Fluida, Layout Base y Cabecera Institucional (`main.css`)
1. **Tipografía Fluida y Protección contra Zoom en iOS:**
   - Establecer títulos con `clamp()` para escalado suave (ej. `h1 { font-size: clamp(1.75rem, 4vw + 1rem, 2.5rem); }`).
   - Forzar `font-size: 16px` (1rem) en campos `<input>`, `<select>` y `<textarea>` en pantallas de móvil para evitar que iOS Safari realice zoom automático molesto al hacer foco.
2. **Top-Bar Institucional:**
   - En pantallas &le; 640px, simplificar u ocultar elementos secundarios del cintillo superior para evitar desbordes y cortes de línea antiestéticos.
3. **Cabecera (`.site-header`):**
   - En móviles (&le; 480px), reducir el tamaño del logo institucional (`40px`), mostrar siglas compactas `IIICCD` y ocultar o condensar el subtítulo largo.
   - Drawer de navegación móvil con sombra profunda, animación de entrada suave (*slide-down*), `overflow-y: auto` y altura máxima calculada (`max-height: calc(100vh - 76px)`).
4. **Animación del Botón Hamburguesa (`.menu-toggle`):**
   - Transición fluida de las 3 líneas hacia una 'X' cuando esté en estado activo (`.menu-toggle.active`).

### B. Sistema de Grillas y Componentes Universales (`components.css`)
1. **Grillas Utilitarias Responsivas (Eliminación de estilos rígidos):**
   - `.form-row-responsive-2`: 2 columnas en desktop/laptop &rarr; 1 columna en tablet vertical y móvil (&le; 768px).
   - `.form-row-responsive-3`: 3 columnas en desktop &rarr; 2 en tablet &rarr; 1 columna en móvil.
   - `.grid-split-card`: 2 columnas simétricas o asimétricas con apilado automático en pantallas &le; 850px.
2. **Tablas Responsivas con UX Táctil:**
   - Optimizar `.table-responsive` con `-webkit-overflow-scrolling: touch;`, sombra suave de desplazamiento y márgenes que no desborden la tarjeta contenedora.
3. **Botones y Formularios Táctiles:**
   - Altura mínima garantizada de `44px` en botones e inputs en vistas táctiles (cumplimiento WCAG 2.1 AA).
   - `.btn-block-mobile`: botón con ancho 100% en resoluciones &le; 640px para facilitar el toque con el pulgar.

### C. Páginas y Vistas Institucionales (`pages.css`)
1. **Hero de Inicio:**
   - Ajustar `.hero-grid` para apilarse en orden natural a 1 columna en pantallas &le; 900px.
   - Reducir padding vertical del hero en móviles (`3rem 0`) y reordenar las tarjetas de estadísticas para una lectura vertical fluida.
2. **Catálogo y Tarjetas de Cursos:**
   - Ajustar la grilla `.course-grid` con `minmax(280px, 1fr)` para que en pantallas de 360px a 400px las tarjetas aprovechen el 100% del ancho sin dejar márgenes muertos.
3. **Página de Detalle de Curso:**
   - Reorganizar la vista de 2 columnas (contenido a la izquierda y tarjeta de inscripción/precio fija a la derecha) a 1 columna en pantallas &le; 900px, asegurando que el bloque de inversión y matrícula quede accesible y destacado.
4. **Vistas de Autenticación (`login.html` y `registro.html`):**
   - Ajustar la tarjeta dividida (*Dual Auth Card*) para que en tablets verticales y móviles se convierta en una elegante tarjeta vertical con logo institucional centrado y formulario amplio.
5. **Dashboard de Participante y Admin:**
   - Adaptar las tarjetas de métricas estadísticas (total alumnos, cursos activos, recaudación) para distribuirse en 2 columnas en tablets/móviles y 4 en pantallas grandes.
6. **Estudio de Flyers y Certificados:**
   - Asegurar que la vista previa del afiche y el lienzo de diseño se escalen de manera proporcional (`max-width: 100%; height: auto;`) sin romper el layout en pantallas de laptop pequeñas (1366px y 1024px).

### D. Interactividad de Navegación Móvil (`main.js`)
1. **Gestión Integral del Menú Móvil:**
   - Vincular evento click al botón `.menu-toggle` para alternar la clase `show` en `.main-nav` y `active` en `.menu-toggle`.
   - Cerrar automáticamente el menú móvil cuando el usuario haga clic en cualquier enlace (`.nav-link` o botón de autenticación).
   - Cerrar automáticamente el menú si el usuario pulsa en cualquier área fuera de la barra de navegación (detección de clic exterior / backdrop táctil).
   - Restaurar el menú cuando se redimensiona la pantalla a tamaños de escritorio.

### E. Limpieza de Grillas Rígidas Inline en Plantillas Thymeleaf
Sustituir los atributos inline `style="display: grid; grid-template-columns: ..."` por las nuevas clases responsivas en:
1. `src/main/resources/templates/admin/cursos/form.html` (fechas, cupos, precio, horas).
2. `src/main/resources/templates/public/curso-detalle.html` (metadatos de docentes, fechas, certificación).
3. `src/main/resources/templates/public/quienes-somos.html` (misión, visión y directiva institucional).
4. `src/main/resources/templates/public/contacto.html` (datos de contacto y formulario de mensajes).
5. `src/main/resources/templates/participante/inscripcion.html` (instrucciones y código QR de Yape).
6. `src/main/resources/templates/admin/inscripciones/detalle.html` (datos de postulante y voucher).
7. `src/main/resources/templates/verificar/certificado.html` (información del certificado verificado).

---

## 4. Plan de Verificación y Control de Calidad
1. **Pruebas Automatizadas de Backend:**
   - Ejecutar `./gradlew test` verificando que las 31 pruebas unitarias sigan pasando al 100%.
2. **Inspección Visual Multi-Resolución:**
   - Validar emulación en resoluciones clave:
     - 1920x1080 (Desktop Full HD)
     - 1366x768 (Laptop estándar)
     - 1024x768 (Tablet horizontal / iPad)
     - 768x1024 (Tablet vertical)
     - 390x844 (iPhone 12/13/14/15)
     - 360x800 (Samsung Galaxy Android)
3. **Validación de Interacciones Táctiles:**
   - Apertura y cierre del menú hamburguesa, accesibilidad de botones, legibilidad de textos sin scroll horizontal indeseado (*zero horizontal overflow*).
4. **Despliegue y Registro:**
   - Commit semántico, subida a la rama `main` en GitHub para compilación en Render y actualización del `memory-bank`.
