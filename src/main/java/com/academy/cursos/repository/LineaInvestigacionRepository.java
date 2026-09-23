package com.academy.cursos.repository;

import com.academy.cursos.model.LineaInvestigacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LineaInvestigacionRepository extends JpaRepository<LineaInvestigacion, Long> {
    List<LineaInvestigacion> findAllByOrderByOrdenAsc();
    Optional<LineaInvestigacion> findByNombre(String nombre);
}
