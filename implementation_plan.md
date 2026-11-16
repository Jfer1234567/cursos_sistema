# Plan de Implementación: Integración Visual del Logotipo Institucional IIICCD

## 1. Diagnóstico del Problema (Enfoque UI/UX Senior)

Actualmente, el logotipo en el encabezado principal ([fragments/header.html](file:///d:/cursos_sistema/src/main/resources/templates/fragments/header.html)) y en el encabezado de autenticación ([fragments/header-auth.html](file:///d:/cursos_sistema/src/main/resources/templates/fragments/header-auth.html)) utiliza el archivo `logo-iiiccd.jpeg`. 

Este archivo presenta tres problemas de integración visual:
1. **Fondo Blanco Opaco (Recuadro Pegado)**: Al ser un archivo JPEG sin canal alfa, se muestra como un bloque rectangular blanco rígido que rompe la estética de cristal traslúcido (*glassmorphism*) del encabezado (`.site-header`).
2. **Sombra Artificial Perimetral**: En [main.css](file:///d:/cursos_sistema/src/main/resources/static/css/main.css), la clase `.brand-logo` tiene aplicada la propiedad `filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.08))`. Como la imagen JPEG es un rectángulo blanco opaco, la sombra se dibuja alrededor de todo el cuadrado, generando el efecto indeseado de una tarjeta o etiqueta pegada encima del navbar.
3. **Redundancia Visual**: El archivo JPEG contenía en su parte inferior una barra gris con las letras "IIICCD", compitiendo con el texto institucional tipográfico adyacente (`<span class="brand-acronym">IIICCD</span>`).

---

## 2. Arquitectura de Solución UI/UX

### A. Recursos Gráficos con Transparencia Pura (PNG / SVG)
- Generar y estandarizar el logotipo oficial transparente:
  - `src/main/resources/static/images/logo-iiiccd.png`: Alta resolución (885x776 px) con canal alfa nativo ARGB al 100%, conservando la red neuronal y esferas institucionales en granate oficial UNA Puno sin pedestal gris ni fondo blanco.
  - `src/main/resources/static/images/logo-iiiccd.svg`: Recurso vectorial escalable para máxima nitidez en pantallas de ultra alta densidad (Retina / 4K).

### B. Eliminación de Efectos y Estilos Invasivos en CSS ([main.css](file:///d:/cursos_sistema/src/main/resources/static/css/main.css))
- **Eliminar totalmente `filter: drop-shadow(...)`** en `.brand-logo`.
- **Eliminar cualquier fondo, borde o contorno** sobre el logotipo.
- Configurar `.brand-logo`:
  - `height: 42px; width: auto; max-height: 42px; object-fit: contain;`
  - `display: block; background: transparent; border: none; box-shadow: none;`
- Calibrar la relación espacial y alineación vertical con el texto institucional:
  - `.brand-link`: `display: inline-flex; align-items: center; gap: 0.85rem;`
  - `.brand-info`: `display: flex; flex-direction: column; justify-content: center; line-height: 1.1;`
  - `.brand-acronym`: Tipografía oficial con `font-weight: 800; font-size: 1.35rem; color: var(--primary); letter-spacing: 0.5px;`
  - `.brand-sub`: Subtítulo `Inteligencia Computacional & Ciencia de Datos` con `font-size: 0.72rem; color: var(--text-muted); font-weight: 500;`

### C. Aplicación Consistente en Toda la Plataforma
1. **Encabezado Principal ([fragments/header.html](file:///d:/cursos_sistema/src/main/resources/templates/fragments/header.html))**: Enlazar el nuevo `logo-iiiccd.png` transparente.
2. **Encabezado de Autenticación ([fragments/header-auth.html](file:///d:/cursos_sistema/src/main/resources/templates/fragments/header-auth.html))**: Enlazar el logotipo transparente con la misma jerarquía.
3. **Pie de Página ([fragments/footer.html](file:///d:/cursos_sistema/src/main/resources/templates/fragments/footer.html))**: Integrar el logotipo transparente de forma sobria en la primera columna institucional (`IIICCD - FINESI`) para reforzar la identidad corporativa.
4. **Pantallas de Login y Registro ([auth/login.html](file:///d:/cursos_sistema/src/main/resources/templates/auth/login.html) y [auth/registro.html](file:///d:/cursos_sistema/src/main/resources/templates/auth/registro.html))**: Estandarizar el uso del recurso oficial transparente.
5. **Favicon e Icono de Navegador ([layout/main.html](file:///d:/cursos_sistema/src/main/resources/templates/layout/main.html))**: Apuntar al icono PNG transparente e incrementar la versión de assets a `v=14` para invalidación de caché.

### D. Adaptabilidad Móvil y Responsive
- En pantallas móviles (`max-width: 768px`):
  - `.brand-logo`: escalado suave a `height: 36px;`
  - `.brand-acronym`: `font-size: 1.15rem;`
  - `.brand-sub`: oculto o ajustado para evitar saltos de línea en dispositivos compactos.

---

## 3. Archivos a Modificar

| Archivo | Cambio Principal |
| :--- | :--- |
| [src/main/resources/static/images/logo-iiiccd.png](file:///d:/cursos_sistema/src/main/resources/static/images/logo-iiiccd.png) | Generación del logotipo institucional transparente en alta resolución (885x776). |
| [src/main/resources/static/images/logo-iiiccd.svg](file:///d:/cursos_sistema/src/main/resources/static/images/logo-iiiccd.svg) | Creación del asset SVG transparente. |
| [src/main/resources/static/css/main.css](file:///d:/cursos_sistema/src/main/resources/static/css/main.css) | Eliminación de drop-shadow y recuadro; ajuste de proporciones, alineación vertical y gap óptico. |
| [src/main/resources/templates/fragments/header.html](file:///d:/cursos_sistema/src/main/resources/templates/fragments/header.html) | Uso del logotipo transparente e integración con texto. |
| [src/main/resources/templates/fragments/header-auth.html](file:///d:/cursos_sistema/src/main/resources/templates/fragments/header-auth.html) | Uso del logotipo transparente en cabecera de login/registro. |
| [src/main/resources/templates/fragments/footer.html](file:///d:/cursos_sistema/src/main/resources/templates/fragments/footer.html) | Integración visual del logotipo institucional transparente en el footer. |
| [src/main/resources/templates/layout/main.html](file:///d:/cursos_sistema/src/main/resources/templates/layout/main.html) | Favicon transparente e incremento a `v=14`. |

---

## 4. Plan de Verificación

1. **Inspección Visual en Navegador**:
   - Verificar que el logotipo se fusione sin recuadro blanco ni sombras perimetrales sobre el fondo traslúcido del navbar.
   - Comprobar la alineación visual con el texto "IIICCD" y el subtítulo.
   - Probar en escritorio, tablet y vista móvil (responsive).
2. **Verificación de Consistencia Multi-Sección**:
   - Validar Header, Header de Autenticación, Footer y vistas de Login/Registro.
3. **Ejecución de Pruebas Automatizadas**:
   - Ejecutar `$env:JAVA_HOME = "..."; .\gradlew.bat test` para comprobar que la suite de pruebas (31/31) se ejecute exitosamente al 100%.
