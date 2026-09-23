package com.academy.cursos.service;

import com.academy.cursos.model.ConfigPago;
import com.academy.cursos.repository.ConfigPagoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ConfigPagoService {

    private final ConfigPagoRepository configPagoRepository;
    private final ArchivoService archivoService;

    public ConfigPagoService(ConfigPagoRepository configPagoRepository, ArchivoService archivoService) {
        this.configPagoRepository = configPagoRepository;
        this.archivoService = archivoService;
    }

    public ConfigPago obtenerConfigActiva() {
        return configPagoRepository.findFirstByActivoTrue()
                .orElseGet(() -> {
                    ConfigPago defecto = new ConfigPago();
                    defecto.setNombreMetodo("Yape");
                    defecto.setNumeroYape("951234567");
                    defecto.setTitularYape("Leonid Alemán Gonzales");
                    defecto.setInstrucciones("Transfiere por Yape al número o escanea el QR.");
                    defecto.setActivo(true);
                    return configPagoRepository.save(defecto);
                });
    }

    @Transactional
    public ConfigPago actualizarConfig(String numeroYape, String titularYape, String instrucciones, MultipartFile qrFile) throws IOException {
        ConfigPago config = obtenerConfigActiva();
        config.setNumeroYape(numeroYape);
        config.setTitularYape(titularYape);
        config.setInstrucciones(instrucciones);

        if (qrFile != null && !qrFile.isEmpty()) {
            String ruta = archivoService.guardarArchivo(qrFile, "qr");
            config.setQrImagenUrl("/uploads/" + ruta);
        }

        return configPagoRepository.save(config);
    }
}
