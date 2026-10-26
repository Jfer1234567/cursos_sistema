# Plan de Implementación: Secuencia Cronológica de Publicación (Recién Creado Arriba)

## 1. Diagnóstico del Problema
1. **Conflicto con `fechaInicio`:** Al ordenar por la fecha del calendario en que se dicta el curso (`fechaInicio DESC`), si un administrador crea hoy un taller programado para dentro de 10 días, pero ya existía un diplomado guardado para el próximo mes, el diplomado antiguo se quedaba en la posición principal (#1) y el evento recién creado quedaba relegado abajo.
2. **La causa raíz del problema anterior no era `createdAt`, sino `updatedAt`:**
   - En JPA, `@Column(name = "created_at", updatable = false)` es **inmutable**; nunca cambia al editar un curso.
   - Lo que antes causaba que un curso editado subiera a la cima era el uso indebido de `ORDER BY updatedAt DESC`.
3. **Comportamiento requerido:**
   - Todo evento recién creado debe aparecer automáticamente en la cima (#1).
   - Los eventos anteriores bajan de manera ordenada (#2, #3, ...).
   - Si se edita un curso antiguo (precio, descripción, docente), su fecha de creación `createdAt` no cambia, por lo que **NO se moverá de su posición ni subirá a la cima**.

## 2. Archivos a Modificar

1. **`src/main/java/com/academy/cursos/repository/CursoRepository.java`**:
   - Cambiar métodos de consulta a:
     - `findTop6ByEstadoOrderByCreatedAtDesc(EstadoCurso estado)`
     - `findFirstByEstadoAndFlyerPublicadoTrueAndFlyerUrlIsNotNullOrderByCreatedAtDesc(EstadoCurso estado)`
     - `findByFlyerPublicadoTrueAndFlyerUrlIsNotNullOrderByCreatedAtDesc()`
     - En `findByEstadoAndLineaInvestigacion`: Agregar `ORDER BY c.createdAt DESC`.
2. **`src/main/java/com/academy/cursos/service/CursoService.java`**:
   - `obtenerFlyerPrincipal()`: Priorizar el curso con afiche ordenado por `createdAt DESC`.
   - `fallback`: Ordenar los cursos publicados con afiche por `createdAt DESC`:
     `b.getCreatedAt().compareTo(a.getCreatedAt())`.
   - `listarDestacados()`: Consumir `findTop6ByEstadoOrderByCreatedAtDesc`.
3. **`src/main/resources/templates/admin/cursos/form.html`**:
   - En `previewFlyer(event)`: Al seleccionar afiche, asegurar que el checkbox `flyerPublicado` se marque y que si el estado estaba en `BORRADOR`, pase automáticamente a `PUBLICADO` para que el afiche se proyecte de inmediato sin pasos manuales confusos.
4. **`src/test/java/com/academy/cursos/CursoFlyerTest.java`**:
   - Validar que al crear un curso nuevo con flyer, este asuma la cima y que al editarlo conserve su posición.

## 3. Plan de Verificación
1. Ejecutar `./gradlew test` garantizando 100% de éxito en todas las pruebas.
2. Comprobar que:
   - Crear un evento nuevo lo ubique inmediatamente como destacado en la portada.
   - Editar un evento anterior no cambie su orden ni desplace al más reciente.
3. Git commit y push a `origin/main` para despliegue en Render.
