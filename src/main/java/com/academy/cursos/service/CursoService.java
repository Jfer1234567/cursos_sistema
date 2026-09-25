package com.academy.cursos.service;

import com.academy.cursos.dto.CursoDTO;
import com.academy.cursos.model.AreaInvestigacion;
import com.academy.cursos.model.Curso;
import com.academy.cursos.model.enums.EstadoCurso;
import com.academy.cursos.repository.AreaInvestigacionRepository;
import com.academy.cursos.repository.CursoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CursoService {

    private final CursoRepository cursoRepository;
    private final AreaInvestigacionRepository areaRepository;
    private final ArchivoService archivoService;

    public CursoService(
            CursoRepository cursoRepository,
            AreaInvestigacionRepository areaRepository,
            ArchivoService archivoService) {
        this.cursoRepository = cursoRepository;
        this.areaRepository = areaRepository;
        this.archivoService = archivoService;
    }

    public List<Curso> listarPublicados(Long lineaId) {
        return cursoRepository.findByEstadoAndLineaInvestigacion(EstadoCurso.PUBLICADO, lineaId);
    }

    public List<Curso> listarDestacados() {
        return cursoRepository.findTop6ByEstadoOrderByCreatedAtDesc(EstadoCurso.PUBLICADO);
    }

    public List<Curso> listarTodos() {
        return cursoRepository.findAll();
    }

    public Optional<Curso> obtenerPorId(Long id) {
        return cursoRepository.findById(id);
    }

    @Transactional
    public Curso guardarOActualizar(CursoDTO dto) {
        AreaInvestigacion area = areaRepository.findById(dto.getAreaInvestigacionId())
                .orElseThrow(() -> new IllegalArgumentException("Área de investigación no encontrada"));

        Curso curso;
        if (dto.getId() != null) {
            curso = cursoRepository.findById(dto.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Curso no encontrado"));
            // Mantener cupos disponibles coherentes si cambian cupos totales
            int diffCupos = dto.getCuposTotales() - curso.getCuposTotales();
            curso.setCuposDisponibles(Math.max(0, curso.getCuposDisponibles() + diffCupos));
        } else {
            curso = new Curso();
            curso.setCuposDisponibles(dto.getCuposTotales());
        }

        curso.setNombre(dto.getNombre());
        curso.setDescripcionCorta(dto.getDescripcionCorta());
        curso.setDescripcion(dto.getDescripcion());
        curso.setAreaInvestigacion(area);
        curso.setDuracion(dto.getDuracion());
        curso.setDocenteResponsable(dto.getDocenteResponsable());
        curso.setPrecio(dto.getPrecio());
        curso.setCuposTotales(dto.getCuposTotales());
        curso.setEstado(dto.getEstado() != null ? dto.getEstado() : EstadoCurso.BORRADOR);
        curso.setFechaInicio(dto.getFechaInicio());
        curso.setFechaFin(dto.getFechaFin());
        curso.setImagenUrl(dto.getImagenUrl());
        curso.setEnlaceClase(dto.getEnlaceClase());
        curso.setEnlaceWhatsapp(dto.getEnlaceWhatsapp());
        curso.setPrecioComunidad(dto.getPrecioComunidad());
        curso.setCreditos(dto.getCreditos() != null ? dto.getCreditos() : 2);
        curso.setDocenteCargo(dto.getDocenteCargo());

        // Manejo de la foto del docente
        if (dto.getDocenteFotoFile() != null && !dto.getDocenteFotoFile().isEmpty()) {
            try {
                String rutaRelativa = archivoService.guardarArchivo(dto.getDocenteFotoFile(), "docentes");
                curso.setDocenteFotoUrl("/uploads/" + rutaRelativa);
            } catch (Exception e) {
                throw new RuntimeException("Error al procesar la fotografía del docente: " + e.getMessage(), e);
            }
        } else if (dto.getDocenteFotoUrl() != null && !dto.getDocenteFotoUrl().trim().isEmpty()) {
            curso.setDocenteFotoUrl(dto.getDocenteFotoUrl().trim());
        }

        return cursoRepository.save(curso);
    }

    @Transactional
    public void cambiarEstado(Long id, EstadoCurso nuevoEstado) {
        Curso curso = cursoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Curso no encontrado"));
        curso.setEstado(nuevoEstado);
        cursoRepository.save(curso);
    }

    @Transactional
    public void eliminar(Long id) {
        cursoRepository.deleteById(id);
    }
}
