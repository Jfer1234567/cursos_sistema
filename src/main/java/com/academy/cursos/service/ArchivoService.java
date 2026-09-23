package com.academy.cursos.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class ArchivoService {

    private final Path uploadsPath;

    public ArchivoService(@Value("${app.uploads.dir:uploads/}") String uploadsDir) {
        String cleanPath = uploadsDir.replace("file:", "");
        this.uploadsPath = Paths.get(cleanPath).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadsPath);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo inicializar la carpeta de uploads", e);
        }
    }

    public String guardarArchivo(MultipartFile file, String subcarpeta) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("El archivo se encuentra vacío");
        }

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
