package com.academy.cursos.repository;

import com.academy.cursos.model.Usuario;
import com.academy.cursos.model.enums.Rol;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByCorreo(String correo);
    Optional<Usuario> findByNumeroDocumento(String numeroDocumento);
    boolean existsByCorreo(String correo);
    boolean existsByNumeroDocumento(String numeroDocumento);

    long countByRol(Rol rol);

    List<Usuario> findByRolOrderByCreatedAtDesc(Rol rol);

    Page<Usuario> findByRolOrderByCreatedAtDesc(Rol rol, Pageable pageable);

    @Query("SELECT u FROM Usuario u WHERE u.rol = :rol AND (LOWER(u.nombreCompleto) LIKE LOWER(CONCAT('%', :filtro, '%')) OR LOWER(u.numeroDocumento) LIKE LOWER(CONCAT('%', :filtro, '%')) OR LOWER(u.correo) LIKE LOWER(CONCAT('%', :filtro, '%'))) ORDER BY u.createdAt DESC")
    List<Usuario> buscarParticipantes(@Param("rol") Rol rol, @Param("filtro") String filtro);

    @Query("SELECT u FROM Usuario u WHERE u.rol = :rol AND (LOWER(u.nombreCompleto) LIKE LOWER(CONCAT('%', :filtro, '%')) OR LOWER(u.numeroDocumento) LIKE LOWER(CONCAT('%', :filtro, '%')) OR LOWER(u.correo) LIKE LOWER(CONCAT('%', :filtro, '%'))) ORDER BY u.createdAt DESC")
    Page<Usuario> buscarParticipantes(@Param("rol") Rol rol, @Param("filtro") String filtro, Pageable pageable);

    @Query("SELECT DISTINCT u FROM Usuario u JOIN Inscripcion i ON i.usuario = u WHERE u.rol = :rol AND i.curso.id = :cursoId AND (:filtro IS NULL OR :filtro = '' OR LOWER(u.nombreCompleto) LIKE LOWER(CONCAT('%', :filtro, '%')) OR LOWER(u.numeroDocumento) LIKE LOWER(CONCAT('%', :filtro, '%')) OR LOWER(u.correo) LIKE LOWER(CONCAT('%', :filtro, '%'))) ORDER BY u.createdAt DESC")
    Page<Usuario> buscarParticipantesPorCurso(@Param("rol") Rol rol, @Param("cursoId") Long cursoId, @Param("filtro") String filtro, Pageable pageable);

    @Query("SELECT u FROM Usuario u WHERE u.rol = :rol AND NOT EXISTS (SELECT 1 FROM Inscripcion i WHERE i.usuario = u) AND (:filtro IS NULL OR :filtro = '' OR LOWER(u.nombreCompleto) LIKE LOWER(CONCAT('%', :filtro, '%')) OR LOWER(u.numeroDocumento) LIKE LOWER(CONCAT('%', :filtro, '%')) OR LOWER(u.correo) LIKE LOWER(CONCAT('%', :filtro, '%'))) ORDER BY u.createdAt DESC")
    Page<Usuario> buscarParticipantesSinCursos(@Param("rol") Rol rol, @Param("filtro") String filtro, Pageable pageable);

    @Query("SELECT count(u) FROM Usuario u WHERE u.rol = :rol AND NOT EXISTS (SELECT 1 FROM Inscripcion i WHERE i.usuario = u)")
    long countSinInscripciones(@Param("rol") Rol rol);
}
