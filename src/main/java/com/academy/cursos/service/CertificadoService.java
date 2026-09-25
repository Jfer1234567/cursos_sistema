package com.academy.cursos.service;

import com.academy.cursos.model.Certificado;
import com.academy.cursos.model.Inscripcion;
import com.academy.cursos.repository.CertificadoRepository;
import com.academy.cursos.util.CertificadoPdfGenerator;
import com.academy.cursos.util.CodigoVerificacionUtil;
import com.lowagie.text.DocumentException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class CertificadoService {

    private final CertificadoRepository certificadoRepository;
    private final CorreoService correoService;

    public CertificadoService(CertificadoRepository certificadoRepository, CorreoService correoService) {
        this.certificadoRepository = certificadoRepository;
        this.correoService = correoService;
    }

    @Transactional
    public Certificado generarCertificadoParaInscripcion(Inscripcion inscripcion) {
        Optional<Certificado> existente = certificadoRepository.findByInscripcionId(inscripcion.getId());
        if (existente.isPresent()) {
            return existente.get();
        }

        String codigo;
        do {
            codigo = CodigoVerificacionUtil.generarCodigo();
        } while (certificadoRepository.findByCodigoVerificacion(codigo).isPresent());

        Certificado cert = new Certificado();
        cert.setInscripcion(inscripcion);
        cert.setCodigoVerificacion(codigo);
        cert.setFechaEmision(LocalDate.now());

        // Guardar snapshots inmutables
        cert.setNombreParticipante(inscripcion.getUsuario().getNombreCompleto());
        cert.setNombreCurso(inscripcion.getCurso().getNombre());
        cert.setLineaInvestigacion(inscripcion.getCurso().getAreaInvestigacion().getLineaInvestigacion().getNombre());
        cert.setAreaInvestigacion(inscripcion.getCurso().getAreaInvestigacion().getNombre());
        cert.setDocente(inscripcion.getCurso().getDocenteResponsable());
        cert.setDuracion(inscripcion.getCurso().getDuracion());
        cert.setCreditos(inscripcion.getCurso().getCreditos() != null ? inscripcion.getCurso().getCreditos() : 2);

        Certificado guardado = certificadoRepository.save(cert);

        correoService.enviarCertificadoEmitido(inscripcion.getUsuario(), inscripcion.getCurso(), codigo);
        return guardado;
    }

    public Optional<Certificado> obtenerPorCodigo(String codigo) {
        if (codigo == null) return Optional.empty();
        return certificadoRepository.findByCodigoVerificacion(codigo.trim().toUpperCase());
    }

    public Optional<Certificado> obtenerPorInscripcionId(Long inscripcionId) {
        return certificadoRepository.findByInscripcionId(inscripcionId);
    }

    public byte[] descargarPdf(Long certificadoId) throws DocumentException {
        Certificado cert = certificadoRepository.findById(certificadoId)
                .orElseThrow(() -> new IllegalArgumentException("Certificado no encontrado"));
        return CertificadoPdfGenerator.generarCertificadoPdf(cert);
    }

    public List<Certificado> listarTodos() {
        return certificadoRepository.findAll();
    }
}
