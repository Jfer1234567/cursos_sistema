# Plan de Implementación: Manual de Usuario Oficial con Capturas de Pantalla Reales

## 1. Objetivo y Justificación
Generar un **Manual de Usuario Completo, Profesional e Ilustrado con Capturas de Pantalla Reales** del Sistema Web de Gestión de Cursos y Certificación Oficial del Instituto de Investigación en Inteligencia Computacional y Ciencia de Datos (IIICCD - FINESI - UNA Puno).

El documento se generará en formato **Microsoft Word (`.docx`)** en la carpeta `informe/`, complementando el Informe Técnico IEEE 830 previamente elaborado, listo para ser entregado a la directiva del instituto, jurados e ingenieros evaluadores.

---

## 2. Estructura y Módulos del Manual

### Portada y Datos Institucionales
- Logotipo oficial del IIICCD y membrete de la Universidad Nacional del Altiplano - FINESI.
- Título formal: *Manual de Usuario y Operación del Sistema Web de Cursos y Certificación Digital*.
- Metadatos académicos: Versión 1.0, fecha, roles autorizados, ámbito de aplicación.

### Parte 1: Guía del Participante (Estudiante / Investigador)
1. **Acceso al Portal y Catálogo:** Navegación, búsqueda de cursos por línea de investigación y consulta de vacantes/tarifas (Comunidad UNA vs. General).
2. **Registro de Cuenta:** Formulario validado con DNI (8 dígitos) y WhatsApp (9 dígitos).
3. **Inicio de Sesión y Recuperación:** Autenticación protegida con visualización de contraseña.
4. **Ficha de Curso y Matrícula:** Revisión del perfil del docente, temario, horas académicas y créditos.
5. **Proceso de Pago con Yape:** Escaneo del QR institucional, ingreso del código de operación y subida del comprobante (con autocompresión en cliente).
6. **Aula Virtual y Comunidad:** Acceso protegido a los enlaces de clases en vivo (Zoom/Google Meet) y botón para unirse al grupo oficial de WhatsApp tras la aprobación.
7. **Diploma Digital Oficial:** Visualización del certificado con orla de gala, descarga en PDF 300 DPI, botón «Añadir a LinkedIn» y botón para recibirlo por WhatsApp.

### Parte 2: Guía de la Directiva y Operación (Administrador)
1. **Dashboard y Métricas:** Lectura de indicadores en tiempo real (Cursos activos, pagos pendientes, inscripciones aprobadas, certificados emitidos, usuarios y mensajes).
2. **Gestión Integral de Cursos:** Creación, edición, asignación de fotografía y grado del ponente, control de cupos y enlaces de sesión virtual.
3. **Estudio y Generador de Flyers Oficiales (`/admin/flyer`):** Diseño de afiches promocionales en tiempo real, selección de temas cromáticos (Granate, Cyber Dark, Esmeralda), formatos (4:5 y 1:1) y descarga en PNG Ultra HD.
4. **Bandeja de Pagos y Comprobantes Yape (`/admin/inscripciones/pendientes`):** Visualización del comprobante, validación de código de operación, aprobación inmediata con reserva de cupo y rechazo con notas de retroalimentación.
5. **Emisión de Certificados y Notificación WhatsApp:** Marcado de curso completado, generación de snapshots inmutables y botón «📲 Enviar Certificado por WhatsApp» al número del estudiante en un solo clic.
6. **Directorio de Usuarios y Prospección (`/admin/usuarios`):** Búsqueda por DNI/nombre y filtrado reactivo con menú desplegable por curso matriculado o alumnos sin cursos.
7. **Bandeja de Mensajes y Consultas (`/admin/mensajes`):** Atención de dudas enviadas desde el formulario de contacto con respuestas directas por correo (`mailto:`) y WhatsApp (`wa.me`).
8. **Configuración de Cobros Yape y QR (`/admin/config/pago`):** Actualización de número celular, titular, instrucciones y reemplazo del código QR sin reiniciar el servidor.

### Parte 3: Verificación Pública y Preguntas Frecuentes
- Uso del validador público `/verificar` con código único `IIICCD-XXXXXX` o escaneo de QR.
- Matriz de preguntas frecuentes y resolución de incidencias comunes.

---

## 3. Estrategia de Captura de Pantallas
1. **Captura Automatizada:** Se utilizará un script auxiliar en Python o Microsoft Edge headless con resolución de alta densidad (1280x800 y 1440x900) para generar capturas limpias y nítidas de cada vista clave del sistema.
2. **Imágenes Incluidas:**
   - Portal de Inicio y Hero Institucional.
   - Catálogo de Cursos con Badges y Buscador.
   - Detalle del Curso con Ponente y Tarifas.
   - Pantalla de Login Split-Screen Dual-Card.
   - Pantalla de Registro con validaciones numéricas.
   - Panel del Participante («Mis Cursos») con accesos a Zoom y Certificado.
   - Diploma Digital Oficial y Verificador con Sello Dorado.
   - Panel de Administración y Tarjetas de Métricas.
   - Gestión de Cursos y Formulario con Subida de Foto del Docente.
   - Generador de Flyers Oficiales del IIICCD.
   - Bandeja de Pagos Pendientes con Comprobante Yape.
   - Directorio de Usuarios con Menú Desplegable de Cursos.
   - Bandeja de Mensajes de Contacto.
   - Configuración de Cobros Yape y QR.
3. **Formateo en Word:** Cada captura estará enmarcada, centrada, con sombreado sutil institucional y numerada formalmente (*"Figura 1: ..."*, *"Figura 2: ..."*).

---

## 4. Archivos a Crear / Modificar
- `informe/generar_manual_usuario.py` (Script en Python que construye el documento con `python-docx`).
- `informe/capturas/` (Carpeta con las imágenes PNG capturadas en alta resolución).
- `informe/Manual_de_Usuario_Sistema_Cursos_IIICCD.docx` (Documento final generado en Microsoft Word).

---

## 5. Plan de Verificación
1. **Ejecución del Generador:** Ejecutar `python informe/generar_manual_usuario.py`.
2. **Validación de Archivo:** Comprobar que el archivo `.docx` se guarde sin errores de sintaxis ni bloqueos de permisos.
3. **Verificación de Contenido:** Verificar que todas las figuras e imágenes existan, estén correctamente referenciadas en las tablas/celdas y que el tamaño del archivo refleje la inclusión completa de imágenes HD (esperado > 4-8 MB).
4. **Presentación:** Informar al usuario y proporcionarle la ruta exacta para su apertura en Word.
