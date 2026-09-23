package com.academy.cursos.service;

import com.academy.cursos.model.Curso;
import com.academy.cursos.model.Inscripcion;
import com.academy.cursos.model.Pago;
import com.academy.cursos.model.Usuario;
import com.academy.cursos.model.enums.EstadoInscripcion;
import com.academy.cursos.model.enums.EstadoPago;
import com.academy.cursos.model.enums.MetodoPago;
import com.academy.cursos.repository.CursoRepository;
import com.academy.cursos.repository.InscripcionRepository;
import com.academy.cursos.repository.PagoRepository;
import com.academy.cursos.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PagoService {

    private final PagoRepository pagoRepository;
    private final InscripcionRepository inscripcionRepository;
    private final CursoRepository cursoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ArchivoService archivoService;
    private final CorreoService correoService;

    public PagoService(
            PagoRepository pagoRepository,
            InscripcionRepository inscripcionRepository,
            CursoRepository cursoRepository,
            UsuarioRepository usuarioRepository,
            ArchivoService archivoService,
            CorreoService correoService) {
        this.pagoRepository = pagoRepository;
        this.inscripcionRepository = inscripcionRepository;
        this.cursoRepository = cursoRepository;
        this.usuarioRepository = usuarioRepository;
        this.archivoService = archivoService;
        this.correoService = correoService;
    }

    @Transactional
    public Pago registrarPagoYape(Long inscripcionId, String referenciaExterna, MultipartFile comprobante) throws IOException {
        Inscripcion inscripcion = inscripcionRepository.findById(inscripcionId)
                .orElseThrow(() -> new IllegalArgumentException("Inscripción no encontrada"));

        String rutaArchivo = null;
        String nombreOriginal = null;
        if (comprobante != null && !comprobante.isEmpty()) {
            rutaArchivo = archivoService.guardarArchivo(comprobante, "comprobantes");
            nombreOriginal = comprobante.getOriginalFilename();
        }

        Pago pago = pagoRepository.findByInscripcionId(inscripcionId).orElse(new Pago());
        pago.setInscripcion(inscripcion);
        pago.setMonto(inscripcion.getCurso().getPrecio());
        pago.setMetodoPago(MetodoPago.YAPE);
        pago.setReferenciaExterna(referenciaExterna);
        if (rutaArchivo != null) {
            pago.setComprobanteUrl(rutaArchivo);
            pago.setComprobanteNombreOriginal(nombreOriginal);
        }
        pago.setEstado(EstadoPago.PENDIENTE);
        pago.setFechaPago(LocalDateTime.now());

        Pago guardado = pagoRepository.save(pago);

        inscripcion.setEstado(EstadoInscripcion.PENDIENTE_VERIFICACION);
        inscripcion.setNotasAdmin(null);
        inscripcionRepository.save(inscripcion);

        correoService.enviarInscripcionCreada(inscripcion.getUsuario(), inscripcion.getCurso());
        return guardado;
    }

    @Transactional
    public void aprobarPago(Long pagoId, Long adminId) {
        Pago pago = pagoRepository.findById(pagoId)
                .orElseThrow(() -> new IllegalArgumentException("Pago no encontrado"));
        Usuario admin = usuarioRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("Admin no encontrado"));

        pago.setEstado(EstadoPago.APROBADO);
        pago.setFechaVerificacion(LocalDateTime.now());
        pago.setVerificadoPor(admin);
        pagoRepository.save(pago);

        Inscripcion inscripcion = pago.getInscripcion();
        inscripcion.setEstado(EstadoInscripcion.APROBADA);
        inscripcionRepository.save(inscripcion);

        // Descontar cupo
        Curso curso = inscripcion.getCurso();
        if (curso.getCuposDisponibles() > 0) {
            curso.setCuposDisponibles(curso.getCuposDisponibles() - 1);
            cursoRepository.save(curso);
        }

        correoService.enviarPagoAprobado(inscripcion.getUsuario(), curso);
    }

    @Transactional
    public void rechazarPago(Long pagoId, Long adminId, String motivo) {
        Pago pago = pagoRepository.findById(pagoId)
                .orElseThrow(() -> new IllegalArgumentException("Pago no encontrado"));
        Usuario admin = usuarioRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("Admin no encontrado"));

        pago.setEstado(EstadoPago.RECHAZADO);
        pago.setFechaVerificacion(LocalDateTime.now());
        pago.setVerificadoPor(admin);
        pago.setNotas(motivo);
        pagoRepository.save(pago);

        Inscripcion inscripcion = pago.getInscripcion();
        inscripcion.setEstado(EstadoInscripcion.RECHAZADA);
        inscripcion.setNotasAdmin(motivo);
        inscripcionRepository.save(inscripcion);

        correoService.enviarPagoRechazado(inscripcion.getUsuario(), inscripcion.getCurso(), motivo);
    }

    public List<Pago> listarPendientes() {
        return pagoRepository.findByEstado(EstadoPago.PENDIENTE);
    }

    public Optional<Pago> obtenerPorId(Long id) {
        return pagoRepository.findById(id);
    }
}
