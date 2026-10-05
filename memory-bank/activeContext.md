# Contexto Activo (Active Context)

**Estado Actual:**
- Plataforma completamente desarrollada e implementada siguiendo el Plan de Implementación v2.
- Capa de datos, seguridad, servicios de negocio, controladores, vistas Thymeleaf, generador de PDF OpenPDF y suite de pruebas finalizados y verificados al 100%.

**Decisiones de Diseño Clave Implementadas:**
- **Snapshots Inmutables en Certificados:** Al completarse un curso, se copian como texto plano los datos del estudiante, curso, línea, área, docente y duración en la entidad `Certificado`. Esto previene alteraciones históricas si los cursos sufren modificaciones posteriores.
- **Seguridad en Comprobantes de Pago:** Los comprobantes de Yape subidos por los participantes se sirven a través de un endpoint protegido (`/comprobantes/{archivo}`) que restringe la visualización exclusivamente al propietario de la inscripción y a los administradores del sistema.
- **Enlace de Sesión Protegido (`enlaceClase`):** Los enlaces de Zoom o Google Meet solo se exponen en las vistas de los estudiantes cuya inscripción tenga el estado `APROBADA` o `COMPLETADA`.
- **Configuración Dinámica de Cobros (`ConfigPago`):** La directiva puede modificar el número de Yape, el titular, las instrucciones y la imagen del código QR desde el panel de administración sin tocar código ni reiniciar la aplicación.
- **Canal Exclusivo de WhatsApp:** Enlace específico por curso que se entrega únicamente a estudiantes con pago aprobado en su panel personal y en el correo electrónico transaccional.
- **Exportación de Padrón Excel (CSV con BOM UTF-8):** Endpoint administrativo que descarga el padrón completo con nombres, contactos, documento, estado y código de operación con soporte nativo para tildes y caracteres especiales en Microsoft Excel.
- **Doble Esquema Tarifario (Comunidad UNA vs. General):** Distinción visual y transparente para la comunidad universitaria de la UNA Puno y público externo.
- **Valor Curricular Formal (Créditos Universitarios):** Los créditos académicos universitarios se guardan en el snapshot inmutable del certificado, se expresan en el PDF formal y son validados públicamente en `/verificar`.
- **Estudio y Generador de Flyers Oficiales (`/admin/flyer`):** Herramienta integrada en el panel administrativo para crear y personalizar afiches publicitarios de los cursos en tiempo real. Permite alternar 3 temas visuales institucionales (Granate UNA, Cyber Dark y Deep Emerald), formatos vertical (4:5) y cuadrado (1:1), cargar datos automáticos de cualquier curso y descargar el afiche en PNG de alta resolución (2.5x con `html2canvas`) o copiarlo directo al portapapeles para WhatsApp y redes sociales. Incluye soporte interactivo para subir la fotografía profesional del ponente/docente con previsualización circular instantánea y recorte institucional.
- **Perfil Académico y Fotografía del Docente:** Soporte integral para registrar la fotografía (`docenteFotoUrl`) y grado/especialidad académica (`docenteCargo`) de los ponentes. Se almacena de forma persistente en `Curso`, se administra en el formulario (`/admin/cursos/nuevo`, `editar`), se expone en la vista pública (`/cursos/{id}`) y se sincroniza en el generador de flyers.
- **Reorganización Estructural de Navegación y Footer:** El enlace "Quiénes Somos" se removió del navbar superior para dar protagonismo a los cursos y validación, y se ubicó como columna dedicada en el pie de página inmediatamente al costado de "Sede Académica" con enlaces directos a Misión/Visión, Directiva y Docentes.
- **Simplificación de la Portada de Inicio:** Se retiró el bloque de "Líneas de Investigación / Ejes Temáticos" de la página principal (`/`) para dar paso inmediato a los cursos y convocatorias después del Hero institucional, manteniendo la vista exhaustiva en `/lineas-investigacion`.
- **Diseño y Estilo Visual Premium Implementado:**
  - *Navbar Glassmorphism:* Fijado permanente (`position: sticky`) con desenfoque de cristal translúcido y micro-interacciones hover.
  - *Hero Tecnológico:* Malla neuronal vectorial SVG, resplandores multicapa granate UNA / azul noche y tarjeta de métricas con borde cristalino reflectivo.
  - *Portadas y Badges de Cursos:* Tarjetas enriquecidas con portadas temáticas visuales por línea de investigación y badges dinámicos de vacantes/modalidad.
  - *Ficha de Validación Universitaria:* Diploma digital interactivo con medallón dorado oficial animado en relieve, botón para copiar enlace de verificación y botón para añadir la certificación a LinkedIn.
- **Canal Directo de WhatsApp para Estudiantes y Administradores:**
  - *Estudiantes:* Botón `📲 Recibir por WhatsApp` en su panel personal para solicitar su certificado con código único al número oficial del instituto.
  - *Administradores:* Botón `💬 Enviar Confirmación y Bienvenida por WhatsApp` con normalización telefónica peruana y mensaje automático con enlace a clases en vivo y grupo de WhatsApp.

- **Rediseño de Gala del Certificado Oficial (Web y PDF OpenPDF):**
  - *Extracción y Limpieza del Isotipo Institucional:* Se procesó el logotipo oficial `logo-iiiccd.jpeg` (1024x1024), eliminando por completo el pedestal y texto plomo "IIICCD" (corte exacto en `y=802`), transformando el fondo blanco en transparencia ARGB nativa para producir el isotipo puro de red neuronal y esferas (`logo-isotipo-watermark.png`, 885x776 px).
  - *Marca de Agua Translúcida de Honor:* Integrada en la vista web (`pages.css` con opacidad calibrada) y en el motor OpenPDF (`PdfGState` con `fillOpacity: 0.085f` en capa inferior `getDirectContentUnder`), logrando un contraste óptimo donde el texto formal queda 100% nítido y legible.
  - *Orla Ornamental Universitaria:* Doble marco perimetral en Granate UNA (#6D1A24) y Oro Académico (#C99738) con esquinas ornamentales formalmente trazadas.
  - *Bloque de Firmas Oficiales y Validación:* Incorporación formal de las firmas del Director del IIICCD (Dr. Leonid Alemán Gonzales) y Decanatura FINESI, junto al código QR vectorial y enlace directo para validación pública inmediata.
  - *Endpoint Público de Descarga:* Nuevo endpoint `/verificar/certificado/{codigo}/pdf` para permitir la descarga directa del documento oficial tanto desde el panel privado como desde la consulta pública de autenticidad.

- **Simplificación del Navbar para Visitantes No Registrados:**
  - Los enlaces "Líneas" y "Verificar Certificado" en el navbar y el botón secundario "Validar Certificado" en el Hero fueron condicionados con `sec:authorize="isAuthenticated()"` para mostrarse únicamente a usuarios que han iniciado sesión.
  - Para visitantes públicos (anónimos/no registrados), la barra de navegación se mantiene limpia y orientada a la conversión: `Inicio`, `Cursos`, `Contacto` + botones `Ingresar` y `Registrarse`.

- **Métrica y Directorio de Usuarios Registrados en Panel Admin:**
  - Nueva tarjeta de estadísticas en `/admin`: `👥 Usuarios Registrados` que contabiliza a todos los participantes que han creado cuenta en la plataforma.
  - Nuevo módulo `/admin/usuarios` (`AdminUsuarioController` + `admin/usuarios/index.html`) con buscador interactivo por nombre, DNI o correo, datos de contacto, botón para enlace directo a WhatsApp (`https://wa.me/51...`) y conteo de cursos matriculados.
  - Enlace rápido "Usuarios" en la barra de navegación del administrador.

- **Eliminación Total de Emojis y Figuritas Informales:**
  - Para adoptar una estética solemne, académica y rigurosa acorde al Instituto de Investigación (IIICCD) y la Universidad Nacional del Altiplano (UNA Puno), se retiraron todos los emojis pictográficos e informales (📅, 🎓, 🎯, 🔭, 👨‍🏫, 📝, 💼, 📍, 📧, 📱, ⏰, ✉️, 📹, 💬, ⏳, 🔍, 🔗, 📄, ❌, ⚙, 👥, ✅, 🔴, 🔵, 🟢, ⏱, etc.) en todas las vistas del sistema.
  - Los avatares de la directiva institucional fueron reemplazados por iniciales monogramáticas académicas elegantes (`LA`, `AQ`, `RA`) con paleta de colores y bordes institucionales.
  - Los estados vacíos y botones de acción fueron enriquecidos con iconos vectoriales SVG limpios y tipografía clara.
  - Se verificó mediante un script exhaustivo de categorización Unicode en Python que no queda ningún emoji en las plantillas Thymeleaf ni en scripts frontend.

- **Publicación de Flyer Oficial y Carga en Cloudinary:**
  - Se habilitó la carga de afiches promocionales (`flyerFile`) guardados en Cloudinary dentro de la carpeta `cursos_sistema/flyers`.
  - Botón directo "Publicar Flyer" y switch de activación en portada (`/admin/cursos/nuevo` y `editar`).
  - **Vitrina Institucional Flotante y Separación Visual:** A solicitud del usuario para separar el evento superior del resto del inicio, se envolvió el bloque en una tarjeta flotante (`.flyer-vitrina-card`) con esquinas curvadas de 24px, borde dorado perimetral suave, sombra tridimensional y espaciado amplio superior e inferior (`padding: 3.5rem 0 2rem;`), otorgándole un aire de vitrina de gala completamente independiente.
  - **Exclusión y Transición Dinámica de Evento Superior:** El evento que ocupa la posición superior (flyer más reciente / publicado) queda automáticamente excluido de la cuadrícula inferior ("Próximos Cursos y Convocatorias") para evitar duplicación. Al crear o publicar un nuevo evento con flyer, este asume la posición superior y el evento anterior desciende a la cuadrícula inferior automáticamente.

  - *Frontend:* Validación interactiva en tiempo real en `/registro` y `/participante/perfil`. DNI restringido a exactamente 8 dígitos numéricos con bloqueo de letras y símbolos. Teléfono celular restringido a exactamente 9 dígitos numéricos (celular Perú). Adaptación dinámica si se selecciona Pasaporte o Carné de Extranjería (hasta 12 alfanuméricos).
  - *Backend:* Reglas de validación `@Pattern` en `RegistroDTO` y chequeo estricto en `UsuarioService` (`^[0-9]{8}$` y `^[0-9]{9}$`). Mapeo amigable de errores al campo exacto en `AuthController`.

- **Bandeja Oficial de Consultas y Soporte Multicanal (`/admin/mensajes`):**
  - *Formulario Público Conectado:* El formulario de `/contacto` ahora procesa envíos reales con validaciones de servidor (`ContactoDTO`), persistiendo en la tabla `mensajes_contacto` de PostgreSQL con fecha/hora de recepción, remitente, correo, teléfono opcional, asunto y mensaje detallado.
  - *Notificación Asíncrona por Correo:* `CorreoService.enviarNotificacionContacto` despacha una copia al correo institucional (`iiiccd.finesi@gmail.com`) en bloque `try/catch` seguro para asegurar que nunca se pierda un mensaje aun si el servicio SMTP estuviera sin conexión.
  - *Bandeja Administrativa Completa:* Nueva vista `/admin/mensajes` (`AdminMensajeController` + `admin/mensajes/lista.html`) con buscador por remitente, asunto o correo, contador de no leídos/nuevos, badges de estado (`NUEVO`, `Leído`, `Atendido`).
  - *Vista de Detalle y Auto-Lectura:* `/admin/mensajes/{id}` muestra la ficha completa del remitente, mensaje en bloque destacado, y marca automáticamente el mensaje como leído al abrirlo.
  - *Botones de Respuesta Rápida:*
    - `✉️ Responder por Correo`: Enlace `mailto:` prellenado con el correo del remitente y asunto de respuesta.
    - `💬 Responder por WhatsApp`: Si el remitente indicó número telefónico, enlace `https://wa.me/51...` con mensaje de saludo institucional prellenado.
    - `✓ Marcar como Atendido`: Registra el cambio a atendido/respondido con badge verde.
    - `🗑️ Eliminar Mensaje`: Permite limpiar consultas obsoletas o de prueba.
  - *Métricas y Accesos:* Nueva tarjeta `📨 Mensajes y Consultas` en el Dashboard (`/admin`) y enlace directo `Mensajes` en el header del Administrador.

- **Ambientación Visual con Isotipo Puro en Páginas Públicas:**
  - *Extensión de Fondo Institucional (`public-page-wrapper` y `ambient-watermark`):* Se eliminó el fondo blanco plano en **Inicio (`/`)**, **Líneas de Investigación (`/lineas-investigacion`)**, **Catálogo de Cursos (`/catalogo`)**, **Detalle del Curso (`/cursos/{id}`)** y **Validación de Certificados (`/verificar`)**, incorporando degradados radiales tenues y marcas de agua amplias del isotipo institucional puro (`logo-isotipo-watermark.png` — sin pedestal ni plataforma ploma) con opacidad suave (4.5%).
  - *Identidad Coherente:* Toda la experiencia de navegación del estudiante y visitante mantiene la misma atmósfera premium e inmersiva vista en el login y en el certificado de honor.

- **Preparación y Compatibilidad para Despliegue Cloud en Railway.app:**
  - *Puerto Dinámico:* `server.port=${PORT:8085}` permitiendo que la plataforma en la nube inyecte el puerto asignado sin romper el puerto local 8085.
  - *Datasource por Variables de Entorno:* URLs y credenciales de base de datos desacopladas (`SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`) con fallback a PostgreSQL local.
  - *Dockerfile Multi-stage Optimizado:* Contenedor de compilación Gradle con JDK 17 y contenedor de ejecución ultraligero con Eclipse Temurin 17 JRE Alpine y `.dockerignore` para despliegues rápidos y seguros.
- **Sincronización Oficial en GitHub:**
  - Repositorio oficial conectado: `https://github.com/Jfer1234567/cursos_sistema` (rama `main`, sincronizado).

- **Auditoría Integral y Limpieza de Código Pre-Despliegue:**
  - *Eliminación de Residuos Obsoletos:* Removidas sentencias SQL hardcodeadas de depuración en `DataInitializer.java` (`DELETE ... Roque`).
  - *Optimización de Consultas (Cero Streams de Tablas Completas):* Añadidos métodos directos en repositorios (`PagoRepository.findByComprobanteUrl`, `CursoRepository.countByEstadoIn` y `AreaInvestigacionRepository.findByNombre`), evitando cargar tablas completas a memoria en `ComprobanteController`, `AdminDashboardController` y `DataInitializer`.
  - *Desacoplamiento y Limpieza en Controladores:* Incorporado `CursoDTO.fromEntity(Curso c)` en `CursoDTO.java`, simplificando radicalmente el método `editarCurso` de `AdminCursoController.java`.
  - *Clean Code & Imports Explícitos:* Eliminados todos los imports con comodín `.*` en controladores y servicios (`AdminCursoController`, `AdminPagoController`, `AdminMensajeController`, `InscripcionController`, `ArchivoService`, `CertificadoPdfGenerator`).

- **Rediseño Premium de Badges de Estado en Panel Admin:**
  - *Sistema Soft-Tag:* Creado sistema de badges con fondo translúcido suave, micro-bordes y tipografía con contraste WCAG AAA (`.badge-status`, `.badge-status-nuevo`, `.badge-status-leido`, `.badge-status-atendido`).
  - *Micro-Indicadores:* Integrados micro-puntos indicadores animados (`badge-dot-nuevo`) e iconos SVG (`✓`) en lugar de óvalos toscos saturados.
  - *Bandeja Estilo Cliente de Correo Moderno:* Fila no leída con borde lateral izquierdo de 3.5px (`.tr-unread`) y fondo translúcido sutil en `admin/mensajes/lista.html` y cabecera en `detalle.html`.

- **Experiencia del Participante y Suscripción Única (Sincronización de Sesiones Meet/Zoom):**
  - *Bloqueo Estricto de Duplicados:* En `InscripcionService`, se impide formalmente una segunda inscripción para el mismo curso y usuario si el estado actual es `APROBADA`, `COMPLETADA` o `PENDIENTE_VERIFICACION`. Solo se permite regularizar si fue `RECHAZADA` o `PENDIENTE_PAGO`.
  - *Acceso Directo a Sesiones (Zoom / Meet) y WhatsApp:* Cuando un participante tiene su inscripción aprobada o completada:
    - En la cabecera, el botón de cursos pasa a ser `🎓 Mis Cursos`, dirigiéndolo directamente a su panel con el botón de Zoom/Meet y WhatsApp.
    - En la ficha pública del curso (`/cursos/{id}`), el botón de compra es sustituido automáticamente por el botón destacado `📹 Unirse a la Clase en Vivo (Zoom / Meet)` y `💬 Grupo Oficial de WhatsApp`, eliminando cualquier posibilidad de recompra o confusión.
    - En el catálogo general (`/catalogo`), la tarjeta del curso resalta con el badge `✓ Inscrito • Acceso Aprobado` y botón directo a la sala virtual.
  - *Optimización de Consultas (`JOIN FETCH`):* Se optimizó `InscripcionRepository.findByCursoId` con `LEFT JOIN FETCH i.usuario LEFT JOIN FETCH i.pago` para evitar el problema N+1 y eliminar cualquier riesgo de `LazyInitializationException`.

  - *Cursos Completados (Clases Concluidas):* Si el curso ya tiene estado `COMPLETADA`, se oculta automáticamente el bloque de "Acceso a Sesiones Virtuales" (enlace a Meet/Zoom y grupo de WhatsApp), y se retiran los botones de descarga de PDF / Ver Certificado / WhatsApp del dashboard para mantener la tarjeta limpia y sin acciones redundantes.

- **Almacenamiento Cloudinary Integrado (100% Gratuito y sin Tarjeta):**
  - Conectado con la cuenta `fq7lbg0c` (25 GB mensuales de almacenamiento y ancho de banda).
  - Todas las subidas de comprobantes de pago de Yape, fotografías de ponentes y códigos QR se suben automáticamente a Cloudinary en la carpeta `cursos_sistema/` y se almacenan como URLs seguras HTTPS (`https://res.cloudinary.com/fq7lbg0c/...`).
  - Cuenta con fallback resiliente al disco local en caso de ausencia de credenciales o pérdida de conectividad.
  - Vistas adaptadas para soportar de forma híbrida tanto URLs de Cloudinary como rutas locales históricas previas.

- **Publicación de Flyer Oficial en el Inicio (Convocatoria Destacada) - Paquete de Diseño Premium:**
  - *Atmósfera Tecnológica:* Fondo multicapa en `#070B14` con resplandores ambientales (*ambient glow*) granate UNA Puno (`rgba(142, 33, 47, 0.45)`) y dorado ámbar (`rgba(217, 119, 6, 0.25)`), complementado con una trama científica sutil de red neuronal en SVG.
  - *Insignia con Pulso en Vivo:* Badge `.flyer-badge-pulse-wrap` con micro-indicador luminoso animado (`.flyer-pulse-dot` con `@keyframes pulseRing`) que señala `● CONVOCATORIA OFICIAL • VACANTES ABIERTAS`.
  - *Tipografía de Alto Impacto:* Título en 2.5rem con gradiente blanco a dorado ámbar brillante (`#FFFFFF` -> `#FBBF24` -> `#F59E0B`).
  - *Ficha Técnica Glassmorphism:* Caja de datos académicos translúcida con 4 íconos vectoriales SVG nítidos (Docente/Investigador, Calendario, Cronómetro de horas y Medalla de créditos).
  - *Tarifas con Jerarquía Universitaria:* Pastilla esmeralda para Público General y pastilla con gradiente ámbar y etiqueta flotante `Beneficio UNA` para estudiantes/egresados de la UNA Puno.
  - *Botones CTA de Alta Conversión:* Botón granate UNA con resplandor, elevación dinámica y flecha vectorial animada `→`, junto a botón de visualización del afiche en HD con ícono de lupa.
  - *Marco Vitrina y Dimensiones Ampliadas:* Marco con resplandor dorado exterior de 45px/60px, esquinas pulidas y micro-etiqueta superior `Afiche Oficial del Programa`. La imagen ahora aprovecha hasta `700px` de altura máxima (frente a 520px anteriores) con `width: 100%`, cuadrícula balanceada al 50%-50% (`1fr 1fr`), contenedor extendido a `1320px` y padding optimizado a `3.5rem 0`, eliminando los espacios vacíos superior e inferior.
  - *Gestión Rápida en Lista de Cursos:* Columna y botón toggle en `admin/cursos/lista.html` para publicar o retirar el afiche de la portada con un solo clic.

- **Paginación Institucional en Gestión de Cursos (`/admin/cursos`):**
  - *Límite por Página:* Restringido a exactamente 4 cursos por página (`PageRequest.of(page, 4, Sort.by(DESC, "createdAt", "id"))`), manteniendo la tabla compacta, limpia y rápida.
  - *Navegación Intuitiva:* Barra inferior con resumen ("Mostrando X a Y de Z cursos registrados"), botón `← Anterior`, botones numéricos `[ 1 ] [ 2 ] ...` con resalte de página activa, y botón `Siguiente →`.
  - *Acceso Completo Garantizado:* Ningún curso queda oculto ni inaccesible; la página 1 muestra los 4 más recientes y la página 2 permite gestionar los cursos anteriores.

- **Directorio de Usuarios Organizado con Menú Desplegable por Cursos (Dropdown Institucional en `/admin/usuarios`):**
  - *Arquitectura de Filtro por Menú Desplegable:* A sugerencia del usuario para evitar sobrecarga visual de pestañas horizontales y garantizar escalabilidad total con muchos cursos, se integró un `<select name="cursoFiltro">` estilizado en la barra de búsqueda:
    - Opción general: `Todos los Registrados (X alumnos)`.
    - Opción especial: `Sin cursos aún (Y - Oportunidad WhatsApp)` destacada en ámbar para prospección comercial inmediata.
    - Grupo `<optgroup label="Filtrar por Curso Matriculado:">`: lista dinámica de todos los cursos con su nombre y cantidad exacta de matriculados (`[Nombre del Curso] ([Z] alumnos)`).
  - *Filtrado Reactivo Inmediato:* Auto-envío con `onchange="this.form.submit()"` para que al elegir un curso la lista se actualice al instante.
  - *Indicador de Filtro Activo:* Pastilla superior que informa claramente qué curso está filtrado con botón directo `&times; Ver todos los cursos`.
  - *Paginación Persistente a 4 Registros:* Mantiene la paginación a 4 participantes por página preservando los parámetros `cursoFiltro` y `filtro` en todos los enlaces numéricos y botones Anterior/Siguiente.
  - *Columna de Cursos Matriculados:* Muestra badges individuales con el nombre específico de cada curso matriculado.
  - *Consultas Eficientes:* Implementadas consultas JPQL nativas `buscarParticipantesPorCurso`, `buscarParticipantesSinCursos` y `findByUsuarioIdConCurso` con `JOIN FETCH` para evitar el problema N+1.

- **Adaptación Contextual de Botones de Cursos según Rol (*Role-Based UX*):**
  - *Distinción de Rol Administrador:* Cuando un usuario con rol `ADMIN` navega por el portal público:
    - En las tarjetas del Catálogo (`/catalogo`) y Portada (`/`): el botón muestra de forma limpia y profesional **«Ver Detalles»** (y en el afiche destacado **«Ver Detalles del Curso»**), en lugar del texto para participantes *«Ver Detalles e Inscribirme»*.
    - En la Ficha de Detalle del Curso (`/cursos/{id}`): el formulario de matrícula y botón *«Inscribirme Ahora»* se oculta por completo, reemplazándose por una pastilla sobria que indica *«Vista de Administrador: Las opciones de matrícula están activas para participantes»*, previniendo auto-inscripciones accidentales y manteniendo la coherencia institucional.

- **Flujo y Acciones Diferenciadas de Certificados según Rol (Admin vs Participante):**
  - *Dilema Resuelto:* Anteriormente, al abrir un certificado emitido desde `/admin/certificados`, el Administrador veía botones dirigidos al alumno (`← Volver a Mis Cursos` y `📲 Recibir por WhatsApp` hacia el instituto).
  - *Experiencia del Administrador:*
    - Botón de retorno contextual: **«← Volver a Certificados Emitidos»** (`/admin/certificados`).
    - Botón verde principal: **«📲 Enviar Certificado por WhatsApp»** dirigido al número celular registrado del estudiante (`inscripcion.usuario.telefono`), normalizado con código de país (`51`).
    - Mensaje institucional formal prellenado: Saludo formal al alumno, felicitación oficial por haber culminado el curso, código único de verificación y enlace oficial de consulta y descarga en `/verificar?codigo=...`.
    - Manejo de excepciones: Si el alumno no registró celular, el botón muestra `Sin Teléfono Registrado` de forma deshabilitada e informativa.
    - Barra informativa de auditoría: Banner superior que expone Nombre completo, DNI, Correo y Teléfono del alumno titular.
    - Botón de acción rápida en la tabla de `/admin/certificados`: Permite enviar el certificado por WhatsApp al instante desde la lista sin entrar a la vista previa si no es necesario.
  - *Experiencia del Participante:*
    - Mantiene su navegación: **«← Volver a Mis Cursos»** (`/participante/dashboard`).
    - Botón verde: **«📲 Recibir por WhatsApp»** dirigido al número oficial del instituto solicitando el registro/envío del certificado.

**Servidor Local Activo:**
- Aplicación corriendo en segundo plano en `http://localhost:8085` (`task-5244`).
- Base de datos conectada: PostgreSQL `cursos_2`.
- Integridad: 100% de tests pasando (30/30 tests ejecutados exitosamente).
- Servidor sirviendo la aplicación completa con HTTP 200 OK.

**Pendientes / Próximos Pasos:**
- Plan de optimización de rendimiento y fluidez (asincronía de emails con `@Async`, compresión GZIP y reducción de I/O) a solicitud del usuario más adelante.
- Despliegue y configuración en producción.



