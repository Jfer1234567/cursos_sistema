# Etapa 1: Compilación de la aplicación con Gradle y JDK 17
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# Copiar archivos de configuración de Gradle
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Dar permisos de ejecución a gradlew
RUN chmod +x ./gradlew

# Descargar dependencias en caché
RUN ./gradlew dependencies --no-daemon || true

# Copiar el código fuente y compilar el JAR de producción sin correr los tests en el build de contenedor
COPY src src
RUN ./gradlew bootJar --no-daemon -x test

# Etapa 2: Entorno de ejecución ligero con JRE 17
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Crear usuario sin privilegios para mayor seguridad
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Copiar el JAR generado desde la etapa de compilación
COPY --from=build /app/build/libs/*.jar app.jar

# Variables de entorno por defecto
ENV PORT=8085
EXPOSE 8085

# Ejecutar la aplicación Spring Boot
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
