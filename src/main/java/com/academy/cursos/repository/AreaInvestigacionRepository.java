package com.academy.cursos.repository;

import com.academy.cursos.model.AreaInvestigacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AreaInvestigacionRepository extends JpaRepository<AreaInvestigacion, Long> {
    List<AreaInvestigacion> findByLineaInvestigacionId(Long lineaInvestigacionId);
    Optional<AreaInvestigacion> findByNombre(String nombre);
}
