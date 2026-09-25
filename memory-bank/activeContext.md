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

- **Restricción Estricta de Dígitos en Documento y Teléfono:**
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

**Servidor Local Activo:**
- Aplicación corriendo en segundo plano en `http://localhost:8085`.
- Base de datos conectada: PostgreSQL `cursos_2`.
- Integridad: 100% de tests unitarios y de integración aprobados (Gradle test suite exitoso).




**Pendientes / Próximos Pasos:**
- Configuración de las variables de entorno de producción (`MAIL_USERNAME`, `MAIL_PASSWORD`, credenciales de PostgreSQL en servidor definitivo).
- Despliegue en el hosting o infraestructura asignada por la universidad.

