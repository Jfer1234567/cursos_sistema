package com.academy.cursos.service;

import com.academy.cursos.model.Curso;
import com.academy.cursos.model.Inscripcion;
import com.academy.cursos.model.Usuario;
import com.academy.cursos.model.enums.EstadoCurso;
import com.academy.cursos.model.enums.EstadoInscripcion;
import com.academy.cursos.repository.CursoRepository;
import com.academy.cursos.repository.InscripcionRepository;
import com.academy.cursos.repository.UsuarioRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class InscripcionService {

    private final InscripcionRepository inscripcionRepository;
    private final CursoRepository cursoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CertificadoService certificadoService;
    private final CorreoService correoService;

    public InscripcionService(
            InscripcionRepository inscripcionRepository,
            CursoRepository cursoRepository,
            UsuarioRepository usuarioRepository,
            @Lazy CertificadoService certificadoService,
            @Lazy CorreoService correoService) {
        this.inscripcionRepository = inscripcionRepository;
        this.cursoRepository = cursoRepository;
        this.usuarioRepository = usuarioRepository;
        this.certificadoService = certificadoService;
        this.correoService = correoService;
    }

    @Transactional
    public Inscripcion iniciarInscripcion(Long usuarioId, Long cursoId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new IllegalArgumentException("Curso no encontrado"));

        if (curso.getEstado() != EstadoCurso.PUBLICADO && curso.getEstado() != EstadoCurso.EN_CURSO) {
            throw new IllegalStateException("El curso no se encuentra disponible para inscripciones");
        }

        Optional<Inscripcion> existente = inscripcionRepository.findByUsuarioIdAndCursoId(usuarioId, cursoId);
        if (existente.isPresent()) {
            Inscripcion ins = existente.get();
            if (ins.getEstado() == EstadoInscripcion.APROBADA || ins.getEstado() == EstadoInscripcion.COMPLETADA) {
                throw new IllegalStateException("Ya te encuentras formalmente inscrito y con acceso aprobado a este curso.");
            }
            if (ins.getEstado() == EstadoInscripcion.PENDIENTE_VERIFICACION) {
                throw new IllegalStateException("Tu comprobante ya fue enviado y se encuentra en proceso de verificación por la administración.");
            }
            // Si el curso es gratuito y la inscripción estaba en PENDIENTE_PAGO o RECHAZADA, la aprobamos de inmediato
            if (curso.esGratuito()) {
                ins.setEstado(EstadoInscripcion.APROBADA);
                if (curso.getCuposDisponibles() > 0) {
                    curso.setCuposDisponibles(curso.getCuposDisponibles() - 1);
                    cursoRepository.save(curso);
                }
                Inscripcion guardada = inscripcionRepository.save(ins);
                if (correoService != null) {
                    correoService.enviarInscripcionGratuitaConfirmada(usuario, curso);
                }
                return guardada;
            }
            // Si está PENDIENTE_PAGO o RECHAZADA en curso de pago, permite ingresar para regularizar el voucher
            return ins;
        }

        if (curso.getCuposDisponibles() <= 0) {
            throw new IllegalStateException("No hay cupos disponibles para este curso");
        }

        Inscripcion inscripcion = new Inscripcion();
        inscripcion.setUsuario(usuario);
        inscripcion.setCurso(curso);

        // Si el curso es gratuito (S/ 0.00), matrícula y aprobación inmediata sin Yape
        if (curso.esGratuito()) {
            inscripcion.setEstado(EstadoInscripcion.APROBADA);
            curso.setCuposDisponibles(curso.getCuposDisponibles() - 1);
            cursoRepository.save(curso);
            Inscripcion guardada = inscripcionRepository.save(inscripcion);
            if (correoService != null) {
                correoService.enviarInscripcionGratuitaConfirmada(usuario, curso);
            }
            return guardada;
        }

        inscripcion.setEstado(EstadoInscripcion.PENDIENTE_PAGO);
        return inscripcionRepository.save(inscripcion);
    }

    public Optional<Inscripcion> obtenerPorUsuarioYCurso(Long usuarioId, Long cursoId) {
        return inscripcionRepository.findByUsuarioIdAndCursoId(usuarioId, cursoId);
    }

    public List<Inscripcion> listarPorUsuario(Long usuarioId) {
        return inscripcionRepository.findByUsuarioId(usuarioId);
    }

    public List<Inscripcion> listarPorCurso(Long cursoId) {
        return inscripcionRepository.findByCursoId(cursoId);
    }

    public Optional<Inscripcion> obtenerPorId(Long id) {
        return inscripcionRepository.findById(id);
    }

    public List<Inscripcion> listarPendientesVerificacion() {
        return inscripcionRepository.findByEstado(EstadoInscripcion.PENDIENTE_VERIFICACION);
    }

    @Transactional
    public void marcarCompletada(Long inscripcionId) {
        Inscripcion inscripcion = inscripcionRepository.findById(inscripcionId)
                .orElseThrow(() -> new IllegalArgumentException("Inscripción no encontrada"));

        if (inscripcion.getEstado() != EstadoInscripcion.APROBADA) {
            throw new IllegalStateException("Solo se puede completar una inscripción previamente aprobada");
        }

        inscripcion.setEstado(EstadoInscripcion.COMPLETADA);
        inscripcionRepository.save(inscripcion);

        // Generar certificado automáticamente
        certificadoService.generarCertificadoParaInscripcion(inscripcion);
    }
}
