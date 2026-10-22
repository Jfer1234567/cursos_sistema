# Plan de Implementación: Rediseño Ejecutivo del Panel de Administración (Estilo IIICCD)

## 1. Justificación y Objetivos de Diseño
El usuario solicita transformar el Panel de Administración (`/admin`) adoptando el diseño de la referencia proporcionada (sin la imagen fotográfica de la UNA Puno, utilizando los recursos, datos y funcionalidades que ya existen):
1. **Banner Institucional de Bienvenida:**
   - Línea de acento dorada con etiqueta: `— ÁREA ADMINISTRATIVA`.
   - Título con tipografía sobria académica: `Bienvenido al espacio de gestión académica`.
   - Subtítulo descriptivo: `Administra los cursos, participantes y certificaciones desde un solo lugar.`
   - Barra de herramientas integrada con accesos directos compactos: `Mensajes`, `Alumnos`, `Configurar Yape` y botón primario destacado `+ Nuevo Curso`.
2. **Sección Modular "¿Qué deseas gestionar hoy?":**
   - Encabezado con línea dorada de acento: `— ¿Qué deseas gestionar hoy?`.
   - Grilla elegante de 2 columnas (en desktop/laptop) y 1 columna (en móviles) con 4 tarjetas de módulos interactivos:
     - **Cursos:** Ícono libro granate + descripción *"Crear y actualizar la oferta académica"* + insignia con el contador de cursos activos + flecha interactiva `&rarr;`.
     - **Inscripciones:** Ícono personas granate + descripción *"Revisar solicitudes de participantes"* + insignia con el contador de inscripciones aprobadas + flecha interactiva `&rarr;`.
     - **Pagos:** Ícono recibo granate + descripción *"Validar comprobantes recibidos"* + insignia con el contador de pagos pendientes + flecha interactiva `&rarr;`.
     - **Certificados:** Ícono diploma granate + descripción *"Emitir y consultar constancias"* + insignia con el contador de certificados emitidos + flecha interactiva `&rarr;`.
3. **Bandeja de Pagos de Yape Pendientes:**
   - Mantener la tabla de comprobantes pendientes debajo de los módulos, con diseño armónico y adaptativo para celulares y tablets.

---

## 2. Archivos a Modificar y Cambios Técnicos

### A. Estilos CSS (`src/main/resources/static/css/pages.css`)
- Crear clases específicas para el módulo administrativo:
  - `.admin-welcome-hero`: Contenedor de bienvenida con fondo suave institucional, tipografía jerárquica y línea dorada.
  - `.admin-section-heading`: Título de sección con guión dorado (`font-size: 1.35rem`, peso 700).
  - `.admin-modules-grid`: Grilla `display: grid; grid-template-columns: 1fr 1fr; gap: 1.5rem;` con media query `@media (max-width: 768px) { grid-template-columns: 1fr; }`.
  - `.admin-module-card`: Tarjeta estilizada con borde sutil, fondo blanco, padding generoso, `border-radius: 12px`, transición al hover (`translateY(-2px)`, sombra suave y color de acento).
  - `.admin-module-icon`: Contenedor del ícono SVG con color granate oficial (`#8B1A1A`) y fondo suave.
  - `.admin-module-arrow`: Flecha roja granate con animación sutil de avance al hover.

### B. Plantilla Thymeleaf (`src/main/resources/templates/admin/dashboard.html`)
- Reemplazar la estructura actual por el nuevo diseño basado en la maqueta:
  - Header de bienvenida con tipografía editorial.
  - Grilla de los 4 módulos de gestión con enlaces directos (`/admin/cursos`, `/admin/usuarios`, `/admin/inscripciones/pendientes`, `/admin/certificados`) y badges informativos con los valores de `${cursosActivos}`, `${inscripcionesAprobadas}`, `${pagosPendientes}`, `${certificadosEmitidos}`.
  - Tabla de comprobantes de Yape pendientes en la parte inferior.

### C. Versionamiento de Assets (`src/main/resources/templates/layout/main.html`)
- Actualizar `th:href="@{/css/pages.css(v=4)}"` y `main.css(v=4)` para forzar a los navegadores a invalidar la caché y cargar el nuevo diseño al instante.

---

## 3. Plan de Verificación
1. **Pruebas Automatizadas:** Ejecutar `./gradlew test` asegurando que el 100% de los tests (31/31) pasen.
2. **Pruebas Responsivas:** Verificar que en escritorio se muestre en 2 columnas y en celular en 1 columna.
3. **Despliegue:** Commit y push a GitHub `main` para Render.
