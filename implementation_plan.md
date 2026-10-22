# Plan de Implementación: Aumento del Área Visual del Flyer Destacado

## 1. Diagnóstico del Problema (Por qué se mostraba pequeño)
1. **Contenedor Estrecho (`max-width: 1320px`):** En pantallas medianas y grandes (1080p, laptops), se desperdiciaban más de 300px a los costados en márgenes en blanco.
2. **Reparto Ineficiente del Grid (`1fr 1.3fr`):** La columna de texto de la izquierda acaparaba innecesariamente el 43.5% del ancho (~550px) para unos pocos datos, dejando a la columna del flyer solo ~650px.
   - En una imagen apaisada (16:9), 650px de ancho forzaba una altura de apenas ~365px.
3. **Marco Oscuro Rígido (`background: #0B1120`):** Al usar un fondo casi negro dentro de una sección blanca pura, cualquier flyer con fondo claro o blanco quedaba encajonado entre barras negras superior e inferior, creando una sensación de "pantallita reducida".

## 2. Solución Senior Propuesta
1. **Ampliación del Canvas (`.seccion-flyer-destacado .container`):**
   - Elevar `max-width` a `1560px` con ancho responsivo del `95%`.
2. **Rebalanceo Agresivo del Grid (`.destacado-editorial-grid`):**
   - Columna izquierda (texto y metadata): Ancho compacto y fijo `minmax(360px, 440px)` que preserva la legibilidad perfecta de títulos y datos sin derrochar espacio.
   - Columna derecha (vitrina del flyer): `1fr` (toma todo el espacio restante disponible: ~950px a 1100px en desktop).
3. **Evolución de la Vitrina (`.destacado-editorial-visual` y `.destacado-editorial-img`):**
   - Reemplazar el fondo negro `#0B1120` por `#F8FAFC` o fondo limpio con sombra perimetral de alta gama `box-shadow: 0 20px 50px -6px rgba(15, 23, 42, 0.15)`.
   - Dar altura mínima sustancial (`min-height: 480px`) y permitir expansión vertical hasta `max-height: 750px` con `width: 100%` y `object-fit: contain`.
4. **Invalidación de Caché en Navegadores (`layout/main.html`):**
   - Pasar a `v=9` en `pages.css`, `main.css`, `components.css` y `main.js`.

## 3. Plan de Verificación
1. Compilar y ejecutar `./gradlew test` garantizando 31/31 tests pasando.
2. Hacer commit y push a `main` para que Render despliegue automáticamente.
