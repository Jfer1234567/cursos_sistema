package com.academy.cursos.repository;

import com.academy.cursos.model.Inscripcion;
import com.academy.cursos.model.enums.EstadoInscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InscripcionRepository extends JpaRepository<Inscripcion, Long> {
    List<Inscripcion> findByUsuarioId(Long usuarioId);
    List<Inscripcion> findByCursoId(Long cursoId);
    List<Inscripcion> findByEstado(EstadoInscripcion estado);
    Optional<Inscripcion> findByUsuarioIdAndCursoId(Long usuarioId, Long cursoId);
    long countByEstado(EstadoInscripcion estado);
    long countByUsuarioId(Long usuarioId);
}
