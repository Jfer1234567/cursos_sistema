package com.academy.cursos.repository;

import com.academy.cursos.model.Certificado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CertificadoRepository extends JpaRepository<Certificado, Long> {
    Optional<Certificado> findByCodigoVerificacion(String codigoVerificacion);
    Optional<Certificado> findByInscripcionId(Long inscripcionId);
}
