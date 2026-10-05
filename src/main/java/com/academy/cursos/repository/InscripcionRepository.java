package com.academy.cursos.repository;

import com.academy.cursos.model.Inscripcion;
import com.academy.cursos.model.enums.EstadoInscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InscripcionRepository extends JpaRepository<Inscripcion, Long> {
    List<Inscripcion> findByUsuarioId(Long usuarioId);

    @Query("SELECT DISTINCT i FROM Inscripcion i LEFT JOIN FETCH i.usuario LEFT JOIN FETCH i.pago WHERE i.curso.id = :cursoId")
    List<Inscripcion> findByCursoId(@Param("cursoId") Long cursoId);
    List<Inscripcion> findByEstado(EstadoInscripcion estado);
    Optional<Inscripcion> findByUsuarioIdAndCursoId(Long usuarioId, Long cursoId);
    long countByEstado(EstadoInscripcion estado);
    long countByUsuarioId(Long usuarioId);
    long countByCursoId(Long cursoId);

    @Query("SELECT i FROM Inscripcion i JOIN FETCH i.curso WHERE i.usuario.id = :usuarioId")
    List<Inscripcion> findByUsuarioIdConCurso(@Param("usuarioId") Long usuarioId);
}
