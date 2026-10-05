# Progreso (Progress)

**Tareas Completadas:**
- [x] Configuración inicial y creación de estructura `memory-bank/`.
- [x] Inicialización del proyecto Spring Boot con Gradle (Java 17).
- [x] Configuración de `application.properties` para PostgreSQL, correo y uploads.
- [x] Creación de entidades JPA (`Usuario`, `LineaInvestigacion`, `AreaInvestigacion`, `Curso`, `Inscripcion`, `Pago`, `Certificado`, `ConfigPago`) y enums asociados.
- [x] Implementación de repositorios Spring Data JPA con consultas especializadas.
- [x] Configuración de Spring Security con login por formulario, roles `PARTICIPANTE` y `ADMIN`, y codificación BCrypt.
- [x] Creación del `DataInitializer` para sembrar las 3 líneas, 11 áreas, usuario administrador y configuración inicial de Yape.
- [x] Sistema de diseño CSS modular institucional (`main.css`, `components.css`, `pages.css`) inspirado en los colores de la FINESI y del logo.
- [x] Implementación de todas las páginas públicas:
  - Portal de inicio (`/`) con estadísticas institucionales y destacados.
  - Quiénes Somos (`/quienes-somos`) con reseña y directiva completa.
  - Líneas de Investigación (`/lineas-investigacion`).
  - Catálogo de Cursos (`/catalogo`) con buscador interactivo y filtro por línea.
  - Detalle del curso (`/cursos/{id}`).
  - Contacto institucional (`/contacto`).
- [x] Autenticación y registro con DNI, correo institucional y validaciones Jakarta.
- [x] Panel del Participante (`/participante/dashboard`, `/participante/perfil`, `/participante/certificado/{id}`).
- [x] Flujo de inscripción y cobro con Yape, subida de vouchers y código de operación.
- [x] Panel de Administración completo:
  - Métricas en tiempo real (`/admin`).
  - CRUD de cursos (`/admin/cursos`).
  - Bandeja de revisión de comprobantes de pago (`/admin/inscripciones/pendientes`).
  - Aprobación de pago con deducción de cupos y rechazo con notas explicativas.
  - Marcado manual de curso completado con emisión automática de certificado.
  - Edición en caliente de datos de Yape y QR (`/admin/config/pago`).
  - Histórico de certificados (`/admin/certificados`).
- [x] Motor de generación de certificados en PDF con OpenPDF y código único `IIICCD-XXXXXX`.
- [x] Verificador público de autenticidad de certificados (`/verificar`).
- [x] Suite de pruebas automatizadas con Gradle y H2 ejecutada con 100% de éxito.
- [x] **4 Mejoras Profesionales (Nivel 10/10) Implementadas y Verificadas:**
  - Enlace al grupo oficial de WhatsApp por curso protegido y exclusivo para inscritos aprobados (en dashboard y correo).
  - Exportación de padrón oficial a Excel (CSV con BOM UTF-8 y delimitador `;` para apertura nativa).
  - Diferenciación de tarifas: Público General vs. Comunidad Universitaria UNA Puno con indicación transparente en catálogo, detalle y pago.
  - Horas y créditos universitarios oficiales plasmados en certificados PDF inmutables con OpenPDF, validador público `/verificar` y vistas de estudiante.
- [x] **Optimización y Limpieza del Espacio en Disco:**
  - Depuración del artefacto fat JAR de compilación previa (`build/libs/cursos-0.0.1-SNAPSHOT.jar` de 61.4 MB) y eliminación de archivos plantilla innecesarios (`HELP.md`).
  - Reducción del tamaño total del proyecto de **65.6 MB a 5.63 MB** (código fuente limpio: 1.46 MB), manteniendo el servidor de desarrollo activo sin interrupciones.
- [x] **Estudio de Diseño y Generador de Flyers Oficiales del IIICCD (`/admin/flyer`):**
  - Implementado `AdminFlyerController` con endpoints `/admin/flyer`, `/admin/flyer/curso/{id}` y `/admin/cursos/{id}/flyer`.
  - Diseñada la interfaz gráfica interactiva en `editor.html` con layout dividido: panel de control visual reactivo a la izquierda y lienzo del afiche a la derecha.
  - Implementado `flyer-generator.js` con enlace reactivo bidireccional en tiempo real para textos, badges, docentes, fechas, horas, créditos, inversión y QR.
  - Soporte de 3 temas de color corporativos: Granate UNA Puno (Oficial), Cyber Deep IA (Azul Noche) y Esmeralda Ciencia de Datos.
  - Soporte de formato vertical (4:5 para WhatsApp y feed) y cuadrado (1:1).
  - Motor de exportación a imagen PNG Ultra HD con escala 2.5x (`html2canvas`) y botón para copiado directo al portapapeles.
  - Integración en la navegación administrativa (`header.html`) y botones de acceso directo "🎨 Crear Flyer" en la lista y detalle de cursos.
  - Pruebas automatizadas en `AdminFlyerControllerTest.java` ejecutadas y aprobadas al 100% con Gradle.
- [x] **Fotografía y Perfil del Ponente / Docente Investigador:**
  - Incorporados los campos `docenteFotoUrl` y `docenteCargo` en la entidad `Curso` y en `CursoDTO`.
  - Integrada la subida de archivos en `CursoService` con almacenamiento estructurado en `uploads/docentes/`.
  - Diseñado el control interactivo en `/admin/flyer` con selector de archivo local (`FileReader`), previsualización instantánea en recorte circular con marco temático dorado/azul/verde y botón para restaurar icono institucional.
  - Actualizado el formulario de administración (`admin/cursos/form.html`) con subida de fotografía del ponente y grado académico.
  - Enriquecida la página pública de detalle del curso (`public/curso-detalle.html`) con tarjeta destacada del ponente y su fotografía oficial.
  - Verificado con `./gradlew test` (100% exitoso) y en el servidor local `http://localhost:8085`.
- [x] **Alineación y Compatibilidad del Logotipo Institucional (`logo-iiiccd.jpeg`):**
  - Actualizadas todas las plantillas HTML para enlazar directamente el archivo `logo-iiiccd.jpeg`.
  - Desactivada la cache de Thymeleaf en desarrollo (`spring.thymeleaf.cache=false`).
- [x] **Depuración de Imágenes Sobrantes y Docente Reemplazado:**
  - Depuradas imágenes redundantes en `src/main/resources/static/images`.
  - Reemplazado docente en semillero y base de datos.
- [x] **Eliminación de la Sección "Líneas de Investigación" de la Portada de Inicio:**
  - Removido el bloque de "Líneas de Investigación / Ejes Temáticos" en `src/main/resources/templates/public/index.html`.
  - La portada ahora conecta limpiamente la sección Hero directamente con "Próximos Cursos y Convocatorias".
  - La sección completa y detallada de Líneas de Investigación se mantiene accesible mediante `/lineas-investigacion` y el pie de página.
  - Verificado en vivo en `http://localhost:8085/` y validado con `./gradlew test` (100% exitoso).
- [x] **Paquete de Mejoras de Diseño y Estilo Visual (Impacto Premium):**
  - **Navbar Fijo con Efecto Cristal (Glassmorphism):** Barra superior fijada con `backdrop-filter: blur(14px) saturate(180%)`, fondo semi-translúcido `rgba(255, 255, 255, 0.85)` y micro-interacciones hover en los enlaces de navegación.
  - **Hero Tecnológico Institucional:** Gradientes multicapa en granate UNA y azul noche de Ciencia de Datos, trama vectorial SVG de redes neuronales y nodos interconectados con resplandor ambiental (*ambient glow*) y tarjeta de estadísticas con reflejo cristalino y acentos dorados.
  - **Portadas y Banners Tecnológicos de Cursos:** Implementado el componente `.curso-card-cover` para el Inicio (`/`) y Catálogo (`/catalogo`) con soporte de imagen propia o banner procedimental por línea de investigación, acompañado de badges flotantes dinámicos (*"🔥 ¡Últimos cupos!"*, *"● Inscripciones Abiertas"*, *"100% Virtual"*).
  - **Ficha de Validación con Sello Dorado Oficial:** Enriquecida la página `/verificar` con estética de diploma digital universitario, medallón/sello dorado oficial animado en relieve (*"✓ Verificado Oficialmente"*), botón interactivo para **"Copiar Enlace de Autenticidad"** con notificación toast y botón directo **"Añadir a mi Perfil de LinkedIn"**.
  - Verificación completa con `./gradlew test` (100% exitoso) y servidor en vivo en `http://localhost:8085`.
- [x] **Integración Directa con WhatsApp para Certificados e Inscripciones:**
  - **Para el Estudiante (`Mis Cursos` y `Ver Certificado`):** Botón `📲 Recibir por WhatsApp` en cursos completados que abre conversación oficial (`+51 951 234 567`) con mensaje pre-redactado con nombre, DNI, curso, código único de certificado y enlace de validación.
  - **Para el Administrador (`Revisión de Pagos` y `Lista de Inscripciones`):** Botón `💬 Enviar Confirmación y Bienvenida por WhatsApp` que normaliza el teléfono peruano del participante (`51XXXXXXXXX`) y redacta automáticamente el mensaje oficial con enlace a las clases en vivo (Zoom/Meet), grupo oficial de WhatsApp y acceso a la plataforma. Acceso rápido por botón compacto en la tabla de pagos pendientes.
  - Verificación completa con `./gradlew test` (100% exitoso) y validación en `http://localhost:8085`.



- [x] **Depuración de Imágenes y Desvinculación de Docente:**
  - Depuración de imágenes redundantes en `src/main/resources/static/images/` conservando exclusivamente el logotipo oficial `logo-iiiccd.jpeg` y el QR oficial de Yape `yape-qr-default.png`.
  - Retirada la tarjeta de directiva y representación institucional de **Ing. Roberto Elvis Roque Claros** de la página Quiénes Somos ([quienes-somos.html](file:///d:/cursos_sistema/src/main/resources/templates/public/quienes-somos.html)), manteniéndose los 3 miembros oficiales (Director, Secretario y Tesorero).
  - Eliminado el curso demo y purgados los registros asociados en base de datos PostgreSQL ([DataInitializer.java](file:///d:/cursos_sistema/src/main/java/com/academy/cursos/config/DataInitializer.java)).
  - Actualizada la documentación en [README.md](file:///d:/cursos_sistema/README.md) y [projectBrief.md](file:///d:/cursos_sistema/memory-bank/projectBrief.md).
  - Verificado con `./gradlew test` (100% exitoso) y en el servidor local `http://localhost:8085`.

- [x] **Rediseño Integral y Formal del Certificado Oficial (Web y PDF):**
  - **Aislamiento del Isotipo Institucional:** Se analizó y recortó el logotipo oficial `logo-iiiccd.jpeg` en `y=802` para suprimir totalmente la peana ploma y las letras "IIICCD". Se generó un PNG transparente de alta resolución (`logo-isotipo-watermark.png`) conservando únicamente las ramas y esferas moleculares/neuronales.
  - **Marca de Agua Central:** Incorporada como fondo en la previsualización web del participante y en el lienzo vectorial de OpenPDF (`PdfGState` con opacidad del 8.5% en la capa inferior), garantizando una presencia solemne sin afectar la nitidez del texto.
  - **Orla y Esquinas Ornamentales:** Marco de honor con doble filete Granate UNA (`#6D1A24`) y Oro Académico (`#C99738`), junto a esquinas ornamentadas.
  - **Firmas y Validación en Línea:** Grilla oficial con las firmas del Director del IIICCD (Dr. Leonid Alemán Gonzales) y Decanatura FINESI, código QR generado al instante y nuevo endpoint de descarga pública (`/verificar/certificado/{codigo}/pdf`).
- [x] **Simplificación del Navbar para Visitantes No Registrados:**
  - Ocultados los enlaces de "Líneas" y "Verificar Certificado" en la barra de navegación para usuarios no autenticados (`!isAuthenticated()`).
  - Ocultado el botón secundario "Validar Certificado" en el Hero del portal principal para focalizar la llamada a la acción en "Explorar Cursos".
  - Mantenido el acceso completo para usuarios registrados/autenticados (`isAuthenticated()`).
- [x] **Métrica y Directorio de Usuarios Registrados en el Sistema:**
  - Implementada tarjeta de estadísticas en `/admin`: `👥 Usuarios Registrados` conectada a `UsuarioRepository.countByRol(Rol.PARTICIPANTE)`.
  - Creado el módulo de directorio en `/admin/usuarios` con buscador en tiempo real por Nombre, DNI y Correo, conteo dinámico de cursos matriculados y botón de contacto directo a WhatsApp (`https://wa.me/51...`).
  - Añadido acceso directo "Usuarios" en la barra de navegación del Administrador (`header.html`).
- [x] **Restricción Estricta de Dígitos en Documento y Teléfono:**
  - Limitado el campo DNI en el formulario de registro (`/registro`) a exactamente 8 dígitos numéricos, bloqueando en tiempo real letras y caracteres extraños, con adaptación dinámica a 12 caracteres alfanuméricos si se selecciona CE o Pasaporte.
  - Limitado el campo Teléfono/WhatsApp a exactamente 9 dígitos numéricos en `/registro` y `/participante/perfil`.
  - Validación de backend integrada en `RegistroDTO` y `UsuarioService` con mensajes de error específicos por campo.
  - Nueva suite de pruebas `UsuarioValidationTest.java` aprobada al 100% (`BUILD SUCCESSFUL in 39s`).
- [x] **Bandeja Oficial de Entrada de Mensajes y Consultas de Contacto:**
  - Persistencia real en PostgreSQL (`mensajes_contacto`) mediante la entidad `MensajeContacto` y DTO `ContactoDTO`.
  - Conexión del formulario público `/contacto` con validación exhaustiva de campos y mensaje flash institucional.
  - Notificación por correo electrónico desacoplada y segura hacia `iiiccd.finesi@gmail.com`.
  - Bandeja administrativa completa `/admin/mensajes` con buscador por remitente, asunto o correo, y filtros de estado.
  - Vista de lectura detallada `/admin/mensajes/{id}` con auto-marcado de lectura y botones de respuesta rápida por Correo (`mailto:`) y WhatsApp (`wa.me/51...`).
  - Posibilidad de marcar mensajes como "Atendido" y eliminación directa.
  - Tarjeta de métricas de mensajes no leídos en `/admin` (dashboard) y acceso directo en el navbar (`header.html`).
  - Suite de pruebas completa `MensajeContactoTest.java` creada y aprobada al 100% (`BUILD SUCCESSFUL in 57s`).
- [x] **Logos Oficiales Vectoriales SVG (WhatsApp y Correo):**
  - Reemplazados todos los emojis de texto (`✉️`, `💬`, `📲`) por los isotipos vectoriales SVG oficiales de alta definición tanto para WhatsApp (auricular y globo oficial) como para Correo Electrónico.
  - Aplicado de manera integral en:
    - Botones de respuesta de lectura en [`admin/mensajes/detalle.html`](file:///d:/cursos_sistema/src/main/resources/templates/admin/mensajes/detalle.html).
    - Acciones rápidas de la bandeja en [`admin/mensajes/lista.html`](file:///d:/cursos_sistema/src/main/resources/templates/admin/mensajes/lista.html).
    - Botón de contacto del directorio de alumnos en [`admin/usuarios/index.html`](file:///d:/cursos_sistema/src/main/resources/templates/admin/usuarios/index.html).
    - Enlace a WhatsApp en revisión de pagos en [`admin/inscripciones/detalle.html`](file:///d:/cursos_sistema/src/main/resources/templates/admin/inscripciones/detalle.html) y [`pendientes.html`](file:///d:/cursos_sistema/src/main/resources/templates/admin/inscripciones/pendientes.html).
    - Botón de recepción de certificado por WhatsApp en [`participante/certificado.html`](file:///d:/cursos_sistema/src/main/resources/templates/participante/certificado.html).
- [x] **Optimización y Limpieza del Navbar del Administrador:**
  - Removidos los botones redundantes "Mensajes" y "Usuarios" de la barra de navegación superior ([`fragments/header.html`](file:///d:/cursos_sistema/src/main/resources/templates/fragments/header.html)) para evitar sobrecarga visual.
  - El acceso a estos módulos queda centralizado limpiamente desde el botón principal **Panel Admin** (`/admin`), el cual cuenta con accesos destacados y métricas en tiempo real tanto para la bandeja de mensajes como para el directorio de alumnos.
- [x] **Armonización y Eliminación de Tarjetas Duplicadas en el Dashboard:**
  - Retiradas las tarjetas inferiores redundantes de "Mensajes y Consultas" y "Usuarios Registrados" en [`admin/dashboard.html`](file:///d:/cursos_sistema/src/main/resources/templates/admin/dashboard.html).
  - Los botones de acción rápida superiores ahora integran los íconos vectoriales SVG y badges numéricos dinámicos de conteo (no leídos y total de alumnos).
  - La cuadrícula de métricas principales queda perfectamente balanceada en 4 columnas simétricas: Cursos Activos, Pagos por Verificar, Inscripciones Aprobadas y Certificados Emitidos.
- [x] **Rediseño Visual de Alta Gama de Autenticación (`/login` y `/registro`):**
  - Transformación integral de la experiencia visual de acceso pasando de una caja blanca básica a una arquitectura **Split-Screen / Dual-Card** inspirada en plataformas universitarias y de investigación de primer nivel.
  - Columna izquierda con identidad institucional profunda: degradado azul noche/granate UNA, malla neuronal de fondo, badge dorado `FINESI • UNA PUNO`, isotipo oficial del IIICCD y pilares de acreditación académica (Certificados con QR inmutable, recursos 24/7 y docentes investigadores).
  - Columna derecha con formulario refinado: inputs con íconos vectoriales SVG internos (correo, candado, DNI, teléfono, usuario, institución), iluminación de foco granate sutil y botón interactivo para mostrar/ocultar contraseña (👁️).
  - Botón de submit principal con gradiente granate, elevación dinámica en hover y flecha indicadora SVG.
  - Diseño 100% responsivo para pantallas móviles, tablets y desktops.
  - Verificado con `./gradlew test` (100% de éxito, código de salida 0) y validación de endpoints HTTP 200 en `http://localhost:8085/login` y `/registro`.
- [x] **Cabecera de Autenticación sin Distracciones (`header-auth.html`):**
  - Desacoplamiento de la cabecera minimalista en su propio archivo de plantilla (`fragments/header-auth.html`) para evitar colisiones de selectores en Thymeleaf y corregir la duplicación de cabeceras en la portada.
  - Interfaz enfocada exclusivamente en la conversión y acceso: logotipo oficial a la izquierda, enlace `← Volver al Portal` y botón contextual (`Registrarse` en login / `Ingresar` en registro), eliminando menús distractores.
- [x] **Ambientación Visual con Isotipo Puro en Páginas Públicas:**
  - Incorporación del isotipo institucional puro del IIICCD (`logo-isotipo-watermark.png` — sin pedestal ni plataforma ploma) como marca de agua ambiental suave (4.5% opacidad) en **Inicio (`/`)**, **Líneas de Investigación (`/lineas-investigacion`)**, **Catálogo de Cursos (`/catalogo`)**, **Detalle del Curso (`/cursos/{id}`)** y **Validación de Certificados (`/verificar`)**.
  - Eliminación de la sensación de "fondo blanco plano", dotando a todo el portal de identidad visual y académica de alta gama.
  - Implementación de `.public-page-wrapper` y `.ambient-watermark` con adaptación responsiva.
- [x] **Rediseño Visual Premium de Badges de Estado en el Panel Administrativo:**
  - *Eliminación de Óvalos Saturados:* Erradicadas las manchas toscas verde oscura (`#059669`) y naranja (`#D97706`) con texto ilegible en la bandeja de consultas ([`/admin/mensajes`](file:///d:/cursos_sistema/src/main/resources/templates/admin/mensajes/lista.html)) y detalle ([`admin/mensajes/detalle.html`](file:///d:/cursos_sistema/src/main/resources/templates/admin/mensajes/detalle.html)).
  - *Sistema Soft-Tag:* Etiquetas estilizadas con bordes definidos (6px), fondos translúcidos al 10%, tipografía con contraste WCAG AAA y micro-indicadores (`.badge-status`, `.badge-status-nuevo`, `.badge-status-leido`, `.badge-status-atendido`).
  - *Fila de Correo Profesional:* Fila no leída con borde lateral izquierdo de 3.5px (`.tr-unread`), emulando la estética de bandejas de correo profesionales como Gmail o Linear.
  - *Pruebas Automatizadas:* 100% de éxito en la suite de pruebas (`BUILD SUCCESSFUL in 43s`).
- [x] **Experiencia del Participante, Suscripción Única y Enlaces a Sesiones Meet/Zoom:**
  - *Restricción Estricta de Doble Inscripción:* `InscripcionService.iniciarInscripcion` valida que si el participante ya tiene una inscripción en estado `APROBADA`, `COMPLETADA` o `PENDIENTE_VERIFICACION`, se lance una excepción explicativa impidiendo duplicados. Solo si la inscripción está en `PENDIENTE_PAGO` o `RECHAZADA` permite continuar para regularizar el comprobante.
  - *Enlace Dinámico en la Barra de Navegación:* En `fragments/header.html`, para estudiantes autenticados, el enlace principal de cursos pasa a ser `🎓 Mis Cursos` (`/participante/dashboard`), donde acceden directamente a sus clases y grupos de WhatsApp, manteniendo a su vez la opción de explorar el catálogo general.
  - *Sustitución Inteligente del Botón de Compra:* En la ficha de curso (`public/curso-detalle.html`), si el participante ya fue aprobado, se oculta el botón de inscripción/pago y se muestran los accesos directos a la sesión virtual (`📹 Unirse a la Clase en Vivo (Zoom / Meet)`) y comunidad oficial (`💬 Grupo Oficial de WhatsApp`).
  - *Distintivo en Catálogo:* En `public/catalogo.html`, los cursos donde el alumno ya está matriculado y aprobado destacan con un badge de honor `✓ Inscrito • Acceso Aprobado` y acceso a la sala virtual.
  - *Optimización N+1 con JOIN FETCH:* `InscripcionRepository.findByCursoId` actualizado con `LEFT JOIN FETCH i.usuario LEFT JOIN FETCH i.pago` para evitar `LazyInitializationException` y optimizar la carga de inscripciones en una sola consulta SQL.
  - *Suite de Pruebas:* 100% de éxito en `./gradlew test` (26 pruebas ejecutadas exitosamente, incluyendo test automatizado de restricción de doble inscripción).
- [x] **Publicación de Flyer Oficial y Convocatoria Destacada en Portada:**
  - *Gestión y Flujo del Flyer:* Botón "Publicar Flyer" en el formulario del curso, con subida automática a Cloudinary (`cursos_sistema/flyers/`) y switch para activar/desactivar publicación en portada.
  - *Diseño de Portada Restaurado y Espaciado:* Sección de ancho completo institucional (`.seccion-flyer-destacado`) con gradiente azul noche, bordes dorados, datos académicos, precios diferenciados y visor de afiche en HD.
  - *Separación Visual Equilibrada:* Incorporados márgenes y padding sutiles (`margin-top: 2.5rem; margin-bottom: 2.25rem; padding: 4.75rem 0;`) para que respire de forma armoniosa entre el Hero y los cursos de abajo sin verse pegado.
  - *Pruebas e Integridad:* 100% de tests pasando (27/27) y respuesta 200 OK con estilos verificados en el servidor en tiempo real.
- [x] **Paginación Institucional en Gestión de Cursos (`/admin/cursos`):**
  - *4 Cursos por Página:* Consulta optimizada con `PageRequest.of(page, 4, Sort.by(DESC, "createdAt", "id"))` en `CursoService` y `AdminCursoController`.
  - *Navegación Visual:* Controles de paginación debajo de la tabla con resumen ("Mostrando X a Y de Z"), botones Anterior/Siguiente dinámicos y enlaces numéricos con resalte de página activa.
  - *Pruebas e Integridad:* Suite de pruebas automatizadas al 100% (28/28 tests pasando exitosamente).
- [x] **Directorio de Usuarios Organizado con Menú Desplegable por Cursos (Dropdown Institucional en `/admin/usuarios`):**
  - *Menú Desplegable Compacto y Escalable:* Reemplazada la barra horizontal de pestañas por un selector `<select name="cursoFiltro">` integrado en la barra de búsqueda superior, con opciones para "Todos los Registrados", "Sin cursos aún" y grupos de cursos con cantidades de inscritos.
  - *Filtrado Reactivo y Pastilla Activa:* Selección con actualización instantánea (`onchange="this.form.submit()"`), acompañada de un badge indicador con botón para limpiar el filtro.
  - *Visualización Detallada de Cursos:* Columna "Cursos Matriculados" con badges individuales que muestran los nombres de cada curso en el que está matriculado el alumno.
  - *Paginación Persistente a 4 Registros:* Mantiene la paginación a 4 participantes por página sin perder la selección del desplegable ni los términos de búsqueda.
  - *Suite de Pruebas:* 100% de éxito en `./gradlew test` (30/30 pruebas pasando exitosamente, incluyendo test automatizado de filtrado con menú desplegable).
- [x] **Diferenciación de Certificados por Rol (Admin vs Participante):**
  - *Retorno Contextual:* En [`participante/certificado.html`](file:///d:/cursos_sistema/src/main/resources/templates/participante/certificado.html), el botón de navegación redirige a **«&larr; Volver a Certificados Emitidos»** (`@{/admin/certificados}`) para administradores y a **«&larr; Volver a Mis Cursos»** (`@{/participante/dashboard}`) para participantes.
  - *Envío al Alumno por WhatsApp (Admin):* El botón verde para el Administrador pasa a ser **«📲 Enviar Certificado por WhatsApp»**, dirigido al número telefónico registrado del participante (`inscripcion.usuario.telefono`), normalizado con el prefijo peruano `51`.
  - *Mensaje Institucional Personalizado:* Despacha un saludo formal, felicitaciones por la culminación del curso, código único de validación y enlace directo a la verificación pública (`/verificar?codigo=...`).
  - *Acción Rápida en Lista Admin:* Se incorporó un botón directo de WhatsApp en la tabla de certificados emitidos ([`admin/certificados/lista.html`](file:///d:/cursos_sistema/src/main/resources/templates/admin/certificados/lista.html)) para permitir el envío con un solo clic.
  - *Barra de Auditoría del Titular:* Banner exclusivo para el Administrador con nombre completo, tipo/número de documento, correo y teléfono del estudiante titular del certificado.
  - *Mantenimiento del Flujo del Alumno:* El participante mantiene su botón **«📲 Recibir por WhatsApp»** dirigido al número del instituto para solicitar su comprobante o soporte oficial.
  - *Suite de Pruebas e Integridad:* 100% de éxito en `./gradlew test` (30/30 tests aprobados) y verificación exitosa de respuestas HTTP tanto para rol ADMIN como para PARTICIPANTE.












