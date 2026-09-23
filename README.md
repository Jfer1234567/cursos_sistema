# Sistema Oficial del IIICCD — FINESI / UNA Puno

Plataforma web institucional del **Instituto de Investigación en Inteligencia Computacional y Ciencia de Datos (IIICCD)**, adscrito a la Facultad de Ingeniería Estadística e Informática de la Universidad Nacional del Altiplano - Puno.

---

## 🏛️ Características Principales

1. **Portal Institucional Académico:**
   - Presentación de misión, visión y directiva oficial (*Director: Leonid Alemán Gonzales, Secretario: Ángel Javier Quispe Carita, Tesorero: Renzo Apaza Cutipa, Director de Actividades: Roberto Elvis Roque Claros*).
   - Estructuración de las **3 Líneas de Investigación** y sus **11 Áreas de Especialización**.
   - Catálogo de cursos con buscador reactivo y filtros por línea temática.

2. **Gestión de Inscripciones y Cobro por Yape:**
   - Visualización de datos de pago (número de teléfono, titular y código QR institucional).
   - Carga segura de comprobante/voucher de pago y número de operación por parte del participante.
   - Enlace privado a sesiones virtuales (Zoom / Google Meet) protegido y accesible únicamente para inscripciones con pago **APROBADO**.

3. **Panel de Administración (Directiva):**
   - Métricas en tiempo real de cursos activos, pagos pendientes y certificados emitidos.
   - CRUD de cursos (precios, vacantes, temario, fechas, modalidad y enlace privado).
   - Bandeja de verificación de pagos: revisión visual de comprobantes, aprobación con descuento de cupos o rechazo con observaciones enviadas al participante.
   - Marcado de cursos completados y emisión automática de certificados.
   - Configuración dinámica de los datos de Yape sin reiniciar el sistema.

4. **Certificación Oficial y Validación Pública:**
   - Generación dinámica de certificados de participación en **PDF apaisado (A4)** con membrete universitario, diseño ornamental y snapshots inmutables de datos académicos.
   - Cada certificado cuenta con un código único de autenticidad `IIICCD-XXXXXX`.
   - Módulo público en `/verificar` donde cualquier entidad puede comprobar la validez y registro oficial del certificado.

---

## 💻 Stack Tecnológico

- **Backend:** Spring Boot (Java 17)
- **Build Tool:** Gradle (Groovy DSL)
- **Seguridad:** Spring Security (sesiones basadas en cookies HTTP-only, contraseñas BCrypt)
- **Persistencia:** Spring Data JPA / Hibernate (PostgreSQL en producción, H2 en testing)
- **Frontend:** Thymeleaf + CSS Modular Institucional a medida + JavaScript Vanilla
- **Generación de PDFs:** OpenPDF (fork de iText, LGPL)
- **Notificaciones:** Spring Mail + Gmail SMTP

---

## 🚀 Puesta en Marcha Local

### Prerrequisitos
- Java Development Kit (JDK) 17 o superior.
- PostgreSQL 14+ (con una base de datos creada llamada `cursos_iiiccd`).

### 1. Clonar y configurar base de datos
En PostgreSQL, crea la base de datos:
```sql
CREATE DATABASE cursos_2;
```

Si tus credenciales locales difieren de `postgres/fer123`, configúralas en `src/main/resources/application.properties` o pásalas por variables de entorno:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/cursos_2
spring.datasource.username=postgres
spring.datasource.password=fer123
```

### 2. Ejecutar la aplicación
```bash
./gradlew bootRun
```
La aplicación iniciará en: `http://localhost:8080`

### 3. Ejecutar pruebas automatizadas
```bash
./gradlew test
```

### 4. Generar el ejecutable de producción (JAR)
```bash
./gradlew bootJar
java -jar build/libs/cursos-0.0.1-SNAPSHOT.jar
```

---

## 👤 Cuentas Sembradas por Defecto

Al iniciar por primera vez, el sistema autosembrará:

| Rol | Correo Electrónico | Contraseña | Propósito |
| :--- | :--- | :--- | :--- |
| **ADMIN** | `admin@iiiccd.edu.pe` | `admin123` | Gestión de cursos, verificación de pagos y certificados. |
| **PARTICIPANTE** | `participante@iiiccd.edu.pe` | `participante123` | Pruebas de inscripción, subida de voucher y consulta de certificado. |

---

## 📧 Configuración de Correo (Gmail SMTP)

Para habilitar el envío real de correos electrónicos transaccionales:
1. Genera una contraseña de aplicación en tu cuenta de Google (*Seguridad -> Contraseñas de aplicaciones*).
2. Configura las variables en tu entorno:
   - `MAIL_USERNAME=tu_correo@gmail.com`
   - `MAIL_PASSWORD=tu_contraseña_de_aplicación`
