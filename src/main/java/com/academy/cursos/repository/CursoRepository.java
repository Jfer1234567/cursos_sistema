package com.academy.cursos.repository;

import com.academy.cursos.model.Curso;
import com.academy.cursos.model.enums.EstadoCurso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Long> {
    List<Curso> findByEstado(EstadoCurso estado);

    @Query("SELECT c FROM Curso c WHERE c.estado = :estado AND (:lineaId IS NULL OR c.areaInvestigacion.lineaInvestigacion.id = :lineaId)")
    List<Curso> findByEstadoAndLineaInvestigacion(@Param("estado") EstadoCurso estado, @Param("lineaId") Long lineaId);

    List<Curso> findTop6ByEstadoOrderByCreatedAtDesc(EstadoCurso estado);
}
