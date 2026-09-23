package com.academy.cursos.repository;

import com.academy.cursos.model.Pago;
import com.academy.cursos.model.enums.EstadoPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {
    List<Pago> findByEstado(EstadoPago estado);
    Optional<Pago> findByInscripcionId(Long inscripcionId);
    long countByEstado(EstadoPago estado);
}
