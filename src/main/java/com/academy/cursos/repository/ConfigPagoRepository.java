package com.academy.cursos.repository;

import com.academy.cursos.model.ConfigPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfigPagoRepository extends JpaRepository<ConfigPago, Long> {
    Optional<ConfigPago> findFirstByActivoTrue();
}
