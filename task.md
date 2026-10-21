# Tareas: Opción de Eliminar Curso y Corrección de Cursos Borrador/Finalizados en Inicio

- [x] 1. Corregir consulta del Flyer Destacado en `CursoRepository` y `CursoService` para restringir a `EstadoCurso.PUBLICADO` (evitando que borradores o finalizados aparezcan en el inicio) <!-- id: 40 -->
- [x] 2. Implementar método seguro de eliminación en `CursoService` (validando 0 inscritos) y endpoint `@PostMapping("/{id}/eliminar")` en `AdminCursoController` <!-- id: 41 -->
- [x] 3. Diseñar e integrar la "Zona de Peligro" con botón `Eliminar Curso` y diálogo de confirmación en `admin/cursos/form.html` (al editar) y botón de eliminación en `admin/cursos/lista.html` <!-- id: 42 -->
- [x] 4. Ejecutar suite de pruebas con `./gradlew test` (validando 100% de éxito en tests de cursos) <!-- id: 43 -->
- [x] 5. Commit y push a GitHub para despliegue en Render y actualización de Memory Bank <!-- id: 44 -->
