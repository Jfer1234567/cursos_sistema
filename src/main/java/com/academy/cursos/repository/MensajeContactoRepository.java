package com.academy.cursos.repository;

import com.academy.cursos.model.MensajeContacto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MensajeContactoRepository extends JpaRepository<MensajeContacto, Long> {

    List<MensajeContacto> findAllByOrderByFechaEnvioDesc();

    long countByLeidoFalse();

    List<MensajeContacto> findByLeidoFalseOrderByFechaEnvioDesc();

    @Query("SELECT m FROM MensajeContacto m WHERE " +
           "LOWER(m.nombreCompleto) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(m.asunto) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(m.correo) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "ORDER BY m.fechaEnvio DESC")
    List<MensajeContacto> buscarMensajes(@Param("query") String query);
}
