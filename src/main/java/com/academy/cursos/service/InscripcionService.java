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

    public InscripcionService(
            InscripcionRepository inscripcionRepository,
            CursoRepository cursoRepository,
            UsuarioRepository usuarioRepository,
            @Lazy CertificadoService certificadoService) {
        this.inscripcionRepository = inscripcionRepository;
        this.cursoRepository = cursoRepository;
        this.usuarioRepository = usuarioRepository;
        this.certificadoService = certificadoService;
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
            return existente.get();
        }

        if (curso.getCuposDisponibles() <= 0) {
            throw new IllegalStateException("No hay cupos disponibles para este curso");
        }

        Inscripcion inscripcion = new Inscripcion();
        inscripcion.setUsuario(usuario);
        inscripcion.setCurso(curso);
        inscripcion.setEstado(EstadoInscripcion.PENDIENTE_PAGO);

        return inscripcionRepository.save(inscripcion);
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
