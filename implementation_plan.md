# Plan de Implementación: Botón Eliminar Curso y Exclusión de Borradores en Portada

## 1. Diagnóstico del Problema Reportado
El usuario ha detectado dos comportamientos críticos:
1. **El curso en BORRADOR sigue apareciendo en el Inicio:**
   - En la captura se observa el curso *"CIENCIA DE DATOS 2"* en estado `BORRADOR`.
   - Sin embargo, en la portada (`/`) el afiche de ese curso sigue mostrándose como evento destacado.
   - **Causa raíz:** En `CursoRepository` y `CursoService.obtenerFlyerPrincipal()`, el método `findFirstByFlyerPublicadoTrueAndFlyerUrlIsNotNullOrderByUpdatedAtDesc()` busca únicamente por `flyerPublicado = true`, **sin verificar que `c.estado == EstadoCurso.PUBLICADO`**. Por lo tanto, si el curso fue cambiado a `BORRADOR` o `FINALIZADO`, seguía figurando en la portada.
2. **Falta de la opción Eliminar Curso:**
   - El curso *"CIENCIA DE DATOS 2"* tiene **20/20 cupos libres (0 inscritos)**, por lo que es un curso de prueba que el usuario desea eliminar completamente.
   - Actualmente no existe el botón ni el endpoint para eliminar cursos.

---

## 2. Solución Propuesta

### A. Exclusión Inmediata de Cursos no Publicados en Portada
1. En `CursoRepository.java`:
   - Reemplazar consultas de flyer para exigir `estado = :estado`:
     ```java
     Optional<Curso> findFirstByEstadoAndFlyerPublicadoTrueAndFlyerUrlIsNotNullOrderByUpdatedAtDesc(EstadoCurso estado);
     List<Curso> findByEstadoAndFlyerPublicadoTrueAndFlyerUrlIsNotNullOrderByUpdatedAtDesc(EstadoCurso estado);
     ```
2. En `CursoService.java`:
   - `obtenerFlyerPrincipal()` buscará exclusivamente cursos con `EstadoCurso.PUBLICADO`.
   - En `cambiarEstado(id, nuevoEstado)` y `guardarOActualizar(cursoDTO)`: Si el estado cambia a `BORRADOR` o `FINALIZADO`, apagar automáticamente `flyerPublicado = false` e invalidar la caché `@CacheEvict(value = "flyerPrincipal")`.

### B. Implementación de Eliminación Segura de Cursos
1. En `CursoService.java`:
   - Inyectar `InscripcionRepository`.
   - En `eliminar(Long id)`:
     ```java
     long inscritos = inscripcionRepository.countByCursoId(id);
     if (inscritos > 0) {
         throw new IllegalStateException("No se puede eliminar el curso porque ya cuenta con " + inscritos + " alumno(s) inscrito(s).");
     }
     cursoRepository.deleteById(id);
     ```
2. En `AdminCursoController.java`:
   - Endpoint `@PostMapping("/{id}/eliminar")`:
     - Invoca `cursoService.eliminar(id)`.
     - Si tiene éxito: `redirectAttributes.addFlashAttribute("successMsg", "Curso eliminado exitosamente.")`.
     - Si tiene inscritos o error: `redirectAttributes.addFlashAttribute("errorMsg", e.getMessage())`.
     - Redirige a `/admin/cursos`.

### C. Interfaz de Usuario: "Zona de Peligro" y Acciones Rápidas
1. En `src/main/resources/templates/admin/cursos/form.html`:
   - Cuando se esté editando (`th:if="${cursoDTO.id != null}"`), agregar debajo del formulario una **Zona de Peligro** destacada con fondo rojo suave (`#FEF2F2`) y borde institucional:
     - Título: `Zona de Peligro`.
     - Texto: `Eliminar permanentemente este curso. Esta acción no se puede deshacer y solo está permitida si el curso no cuenta con participantes inscritos.`
     - Botón rojo: `🗑️ Eliminar este Curso` con confirmación nativa `onsubmit="return confirm('¿Estás seguro de que deseas eliminar permanentemente este curso?');"`.
2. En `src/main/resources/templates/admin/cursos/lista.html`:
   - En la columna de "ACCIONES", junto a `Editar`, agregar un botón `Eliminar` (icono de papelera) con confirmación, para poder borrar cursos con 0 inscritos sin necesidad de entrar a editar.

---

## 3. Archivos a Modificar
- `src/main/java/com/academy/cursos/repository/CursoRepository.java` (añadir métodos de flyer con filtro de estado).
- `src/main/java/com/academy/cursos/service/CursoService.java` (lógica de exclusión de flyer y validación de 0 inscritos al eliminar).
- `src/main/java/com/academy/cursos/controller/admin/AdminCursoController.java` (endpoint `POST /{id}/eliminar`).
- `src/main/resources/templates/admin/cursos/form.html` (Zona de Peligro con botón eliminar).
- `src/main/resources/templates/admin/cursos/lista.html` (botón de eliminación en la tabla de cursos).

---

## 4. Plan de Verificación
1. **Pruebas Automatizadas:** Ejecutar `./gradlew test` asegurando que todos los tests pasen al 100%.
2. **Prueba de Eliminación de Curso:** Verificar que un curso con 0 inscritos se elimine limpiamente de la base de datos y de la lista.
3. **Prueba de Integridad:** Verificar que un curso con inscritos arroje mensaje explicativo impidiendo la rotura de la base de datos.
4. **Prueba de Portada:** Verificar que al cambiar un curso a `BORRADOR` o `FINALIZADO`, desaparezca inmediatamente del inicio.
5. **Despliegue:** Sincronizar en GitHub `main` para Render.
