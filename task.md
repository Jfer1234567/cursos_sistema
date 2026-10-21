# Tareas: Soporte Integral para Cursos Gratuitos (Acceso Libre) sin Yape

- [x] 1. Backend: Implementar lógica de curso gratuito en `Curso.java` (`esGratuito()`) y auto-aprobación con deducción de cupos en `InscripcionService.java` <!-- id: 55 -->
- [x] 2. Controlador: Adaptar `InscripcionController.java` para redireccionar directamente a `/participante/dashboard` con mensaje de éxito cuando el curso sea gratuito <!-- id: 56 -->
- [x] 3. Frontend: Adaptar vistas públicas (`catalogo.html`, `curso-detalle.html`, `index.html`) y de administración (`lista.html`, `form.html`) con badges «GRATIS / Acceso Libre» y botón «Inscribirme Gratis» <!-- id: 57 -->
- [x] 4. Pruebas Automatizadas: Crear test unitario en `MejorasProfesionalesTest.java` para verificar la inscripción inmediata y descuento de cupos en cursos de S/ 0.00 <!-- id: 58 -->
- [x] 5. Verificación `./gradlew test` (100%), despliegue en GitHub `main` para Render y actualización de Memory Bank <!-- id: 59 -->
