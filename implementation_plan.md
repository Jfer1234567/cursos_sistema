# Plan de Implementación: Optimización de Usabilidad y Eliminación de Sobrecarga de Botones

## 1. Justificación y Fundamento de Usabilidad (Hick & Nielsen)
El usuario solicita aplicar estándares de usabilidad y accesibilidad para reducir la saturación y sobrecarga visual de botones (*button fatigue / cognitive overload*) en las pantallas administrativas.

### Diagnóstico de Sobrecarga Actual:
1. **Tabla de Cursos (`/admin/cursos`):** Cada fila tiene hasta 4 botones dispersos (`Publicar Flyer`, `Inscritos`, `Editar`, `🗑️ Eliminar`), generando hasta 16 botones en una sola columna.
2. **Navbar del Administrador (`header.html`):** Muestra el botón redundante `Pagos (X)` cuando ya existe `Panel Admin` con las tarjetas de métricas en grande.
3. **Cabecera del Dashboard (`admin/dashboard.html`):** 4 botones compitiendo en línea superior (`Bandeja de Mensajes`, `Directorio de Alumnos`, `+ Nuevo Curso`, `Configurar Yape`).

---

## 2. Solución Propuesta

### A. Rediseño de la Tabla de Cursos (`admin/cursos/lista.html`)
Implementar el estándar de las grandes plataformas (GitHub, Stripe, Linear):
- **Botón Visible Principal:** **`Editar`** (`btn-sm btn-outline-primary`).
- **Menú Contextual Desplegable (`⋮`):** Un botón compacto de tres puntos que despliega un menú flotante con:
  - `👥 Ver Alumnos Inscritos` (`/admin/cursos/{id}/detalle`)
  - `⭐ Mostrar / Quitar de Portada` (toggle de flyer)
  - `🎨 Generar Flyer Oficial` (`/admin/flyer/curso/{id}`)
  - `─────────────────────` (separador)
  - `🗑️ Eliminar Curso` (opción en texto rojo con diálogo de confirmación)
- **Resultado:** De 16 botones dispersos se pasa a **1 botón y 1 selector ordenado**, liberando espacio y permitiendo una tabla limpia y legible.

### B. Depuración del Menú de Navegación (`fragments/header.html`)
- Retirar el enlace redundante `<a th:href="@{/admin/inscripciones/pendientes}">Pagos</a>` en el bloque de autenticación del administrador.
- El navbar para el administrador queda limpio: `Inicio` | `Cursos` | `[ Panel Admin ]` | `[ Salir ]`.

### C. Jerarquía Visual en el Dashboard (`admin/dashboard.html`)
- Mantener **`+ Nuevo Curso`** como el **único botón primario sólido** (`btn-primary`).
- Diseñar los accesos a `Bandeja de Mensajes` y `Directorio de Alumnos` como enlaces de estado refinados o pastillas agrupadas (`nav-pills`), evitando la sensación de 4 botones toscos en hilera.

---

## 3. Archivos a Modificar
- `src/main/resources/templates/admin/cursos/lista.html` (menú contextual desplegable `⋮`).
- `src/main/resources/templates/fragments/header.html` (retirar botón redundante de Pagos).
- `src/main/resources/templates/admin/dashboard.html` (jerarquía visual de botones superiores).

---

## 4. Plan de Verificación
1. **Compilación y Pruebas:** Ejecutar `./gradlew test` asegurando que los 30 tests existentes pasen al 100%.
2. **Verificación Visual:**
   - Comprobar que el menú `⋮` en la tabla de cursos abra y cierre correctamente sin romper la tabla.
   - Comprobar que las acciones (`Inscritos`, `Flyer`, `Eliminar`) sigan funcionando a través del menú.
   - Comprobar que el navbar y el dashboard se vean limpios y ordenados.
3. **Despliegue:** Sincronizar en GitHub `main` para Render.
