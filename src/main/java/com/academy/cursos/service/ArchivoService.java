package com.academy.cursos.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;

@Service
public class ArchivoService {

    private static final Logger log = LoggerFactory.getLogger(ArchivoService.class);

    private final Path uploadsPath;
    private final Cloudinary cloudinary;

    @Value("${cloudinary.cloud-name:}")
    private String cloudName;

    public ArchivoService(
            @Value("${app.uploads.dir:uploads/}") String uploadsDir,
            Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
        String cleanPath = uploadsDir.replace("file:", "");
        Path target = Paths.get(cleanPath).toAbsolutePath().normalize();
        try {
            Files.createDirectories(target);
        } catch (Exception e) {
            log.warn("No se pudo crear la carpeta de uploads en {}: {}. Usando directorio temporal del sistema.", target, e.getMessage());
            try {
                target = Paths.get(System.getProperty("java.io.tmpdir"), "uploads").toAbsolutePath().normalize();
                Files.createDirectories(target);
            } catch (Exception ex) {
                log.error("Tampoco se pudo crear la carpeta temporal de uploads: {}", ex.getMessage());
            }
        }
        this.uploadsPath = target;
    }

    public String guardarArchivo(MultipartFile file, String subcarpeta) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("El archivo se encuentra vacío");
        }

        // 1. Intento de subida a Cloudinary si está configurado con credenciales activas
        if (cloudinary != null && cloudName != null && !cloudName.isBlank() && !cloudName.equals("test-cloud")) {
            try {
                log.info("Subiendo archivo a Cloudinary en carpeta: cursos_sistema/{}", subcarpeta);
                @SuppressWarnings("unchecked")
                Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                        "folder", "cursos_sistema/" + subcarpeta,
                        "resource_type", "auto"
                ));
                String secureUrl = (String) uploadResult.get("secure_url");
                if (secureUrl != null && !secureUrl.isBlank()) {
                    log.info("Archivo subido con éxito a Cloudinary: {}", secureUrl);
                    return secureUrl;
                }
            } catch (Exception e) {
                log.warn("Fallo al subir a Cloudinary, aplicando fallback a disco local: {}", e.getMessage());
            }
        }

        // 2. Almacenamiento local (fallback o entorno de pruebas sin conexión)
        Path targetDir = this.uploadsPath.resolve(subcarpeta);
        Files.createDirectories(targetDir);

        String originalName = file.getOriginalFilename();
        String extension = "";
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf("."));
        }

        String nuevoNombre = UUID.randomUUID() + extension;
        Path targetLocation = targetDir.resolve(nuevoNombre);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        return subcarpeta + "/" + nuevoNombre;
    }

    public Resource cargarComoRecurso(String rutaRelativa) {
        try {
            if (rutaRelativa != null && rutaRelativa.startsWith("http")) {
                return new UrlResource(rutaRelativa);
            }
            Path filePath = this.uploadsPath.resolve(rutaRelativa).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Archivo no encontrado o no legible: " + rutaRelativa);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error en la ruta del archivo: " + rutaRelativa, e);
        }
    }
}
