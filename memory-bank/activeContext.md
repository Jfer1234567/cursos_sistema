# Contexto Activo (Active Context)

**Estado Actual:**
- Plataforma completamente desarrollada e implementada siguiendo el Plan de Implementación v2.
- Capa de datos, seguridad, servicios de negocio, controladores, vistas Thymeleaf, generador de PDF OpenPDF y suite de pruebas finalizados y verificados al 100%.

**Decisiones de Diseño Clave Implementadas:**
- **Snapshots Inmutables en Certificados:** Al completarse un curso, se copian como texto plano los datos del estudiante, curso, línea, área, docente y duración en la entidad `Certificado`. Esto previene alteraciones históricas si los cursos sufren modificaciones posteriores.
- **Seguridad en Comprobantes de Pago:** Los comprobantes de Yape subidos por los participantes se sirven a través de un endpoint protegido (`/comprobantes/{archivo}`) que restringe la visualización exclusivamente al propietario de la inscripción y a los administradores del sistema.
- **Enlace de Sesión Protegido (`enlaceClase`):** Los enlaces de Zoom o Google Meet solo se exponen en las vistas de los estudiantes cuya inscripción tenga el estado `APROBADA` o `COMPLETADA`.
- **Configuración Dinámica de Cobros (`ConfigPago`):** La directiva puede modificar el número de Yape, el titular, las instrucciones y la imagen del código QR desde el panel de administración sin tocar código ni reiniciar la aplicación.

**Pendientes / Próximos Pasos:**
- Configuración de las variables de entorno de producción (`MAIL_USERNAME`, `MAIL_PASSWORD`, credenciales de PostgreSQL).
- Despliegue en el servidor o infraestructura asignada por la universidad.
