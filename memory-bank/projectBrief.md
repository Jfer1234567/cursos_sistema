# Resumen del Proyecto (Project Brief)

**Proyecto:** Plataforma Web Oficial del Instituto de Investigación en Inteligencia Computacional y Ciencia de Datos (IIICCD).  
**Institución:** Facultad de Ingeniería Estadística e Informática (FINESI) - Universidad Nacional del Altiplano (UNA Puno).

## Propósito y Objetivos
Proveer una plataforma académica moderna para la publicación de cursos de especialización y posgrado organizados por líneas de investigación, gestión de inscripciones con cobro manual vía Yape, validación de comprobantes por parte de la directiva y emisión automatizada de certificados de participación en PDF con código único de verificación pública.

## Arquitectura y Tecnologías
- **Backend:** Spring Boot (Java 17) con Spring Security, Spring Data JPA, Jakarta Validation y Spring Mail.
- **Frontend:** Thymeleaf con Server-Side Rendering, CSS modular institucional a la medida (variables HSL/Hex, diseño sobrio académico), JavaScript vanilla para interactividad reactiva.
- **Base de Datos:** PostgreSQL para producción / H2 para pruebas automatizadas.
- **Generación de Documentos:** OpenPDF (fork de iText bajo licencia LGPL) para certificados de participación oficiales con firma y código verificable.
- **Almacenamiento:** Sistema de archivos seguro con controlador de acceso condicional para comprobantes de pago.
- **Notificaciones:** Servicio de correo mediante Gmail SMTP para alertas transaccionales (registro, inscripción, aprobación, rechazo y emisión de certificados).

## Estructura Organizacional Mapeada
- **Directiva:**
  - Director: Leonid Alemán Gonzales
  - Secretario: Ángel Javier Quispe Carita
  - Tesorero: Renzo Apaza Cutipa
  - Director de Actividades: Roberto Elvis Roque Claros
- **Líneas y Áreas de Investigación:**
  1. *Ciencias de la Computación:* Inteligencia Artificial, Computación de Alto Desempeño, Desarrollo de Algoritmos Complejos, Procesamiento de Imágenes.
  2. *Ciencia de Datos:* Aprendizaje Automático, Ingeniería y Analítica de Datos, Modelos Estocásticos Avanzados, Estadística Bayesiana.
  3. *Investigación:* Estadística Aplicada a la Investigación, Investigación e Innovación Tecnológica, Metodologías de la Investigación.
