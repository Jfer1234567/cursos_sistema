package com.academy.cursos.service;

import com.academy.cursos.dto.CursoDTO;
import com.academy.cursos.model.AreaInvestigacion;
import com.academy.cursos.model.Curso;
import com.academy.cursos.model.enums.EstadoCurso;
import com.academy.cursos.repository.AreaInvestigacionRepository;
import com.academy.cursos.repository.CursoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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

    @Cacheable(value = "cursosPublicados", key = "#lineaId != null ? #lineaId : -1")
    public List<Curso> listarPublicados(Long lineaId) {
        return cursoRepository.findByEstadoAndLineaInvestigacion(EstadoCurso.PUBLICADO, lineaId);
    }

    @Cacheable(value = "cursosDestacados")
    public List<Curso> listarDestacados() {
        return cursoRepository.findTop6ByEstadoOrderByCreatedAtDesc(EstadoCurso.PUBLICADO);
    }

    public List<Curso> listarTodos() {
        return cursoRepository.findAll();
    }

    public Page<Curso> listarPaginado(int pagina, int tamanio) {
        Pageable pageable = PageRequest.of(Math.max(0, pagina), tamanio, Sort.by(Sort.Direction.DESC, "createdAt", "id"));
        return cursoRepository.findAll(pageable);
    }

    public Optional<Curso> obtenerPorId(Long id) {
        return cursoRepository.findById(id);
    }

    @Transactional
    @CacheEvict(value = {"cursosPublicados", "cursosDestacados", "flyerPrincipal"}, allEntries = true)
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
                curso.setDocenteFotoUrl(rutaRelativa.startsWith("http") ? rutaRelativa : "/uploads/" + rutaRelativa);
            } catch (Exception e) {
                throw new RuntimeException("Error al procesar la fotografía del docente: " + e.getMessage(), e);
            }
        } else if (dto.getDocenteFotoUrl() != null && !dto.getDocenteFotoUrl().trim().isEmpty()) {
            curso.setDocenteFotoUrl(dto.getDocenteFotoUrl().trim());
        }

        // Manejo del Flyer oficial del curso
        if (dto.getFlyerFile() != null && !dto.getFlyerFile().isEmpty()) {
            try {
                String rutaFlyer = archivoService.guardarArchivo(dto.getFlyerFile(), "flyers");
                curso.setFlyerUrl(rutaFlyer.startsWith("http") ? rutaFlyer : "/uploads/" + rutaFlyer);
            } catch (Exception e) {
                throw new RuntimeException("Error al procesar el flyer publicitario: " + e.getMessage(), e);
            }
        } else if (dto.getFlyerUrl() != null && !dto.getFlyerUrl().trim().isEmpty()) {
            curso.setFlyerUrl(dto.getFlyerUrl().trim());
        }

        // Estado de publicación del flyer en el Inicio
        boolean publicarFlyer = dto.getFlyerPublicado() != null && dto.getFlyerPublicado();
        if (publicarFlyer) {
            // El nuevo evento ocupa la posición superior; los eventos anteriores bajan a la lista inferior
            List<Curso> anteriores = cursoRepository.findByFlyerPublicadoTrueAndFlyerUrlIsNotNullOrderByUpdatedAtDesc();
            for (Curso ant : anteriores) {
                if (curso.getId() == null || !ant.getId().equals(curso.getId())) {
                    ant.setFlyerPublicado(false);
                    cursoRepository.save(ant);
                }
            }
        }
        curso.setFlyerPublicado(publicarFlyer);

        return cursoRepository.save(curso);
    }

    @Cacheable(value = "flyerPrincipal")
    public Optional<Curso> obtenerFlyerPrincipal() {
        Optional<Curso> flyer = cursoRepository.findFirstByFlyerPublicadoTrueAndFlyerUrlIsNotNullOrderByUpdatedAtDesc();
        if (flyer.isPresent()) {
            return flyer;
        }
        // Fallback: si no hay ninguno marcado explícitamente pero hay cursos publicados con afiche,
        // tomar el curso publicado más reciente con afiche.
        return cursoRepository.findByEstado(EstadoCurso.PUBLICADO).stream()
                .filter(c -> c.getFlyerUrl() != null && !c.getFlyerUrl().trim().isEmpty())
                .sorted((a, b) -> {
                    if (b.getCreatedAt() == null || a.getCreatedAt() == null) return 0;
                    return b.getCreatedAt().compareTo(a.getCreatedAt());
                })
                .findFirst();
    }

    public List<Curso> obtenerFlyersPublicados() {
        return cursoRepository.findByFlyerPublicadoTrueAndFlyerUrlIsNotNullOrderByUpdatedAtDesc();
    }

    @Transactional
    @CacheEvict(value = {"cursosPublicados", "cursosDestacados", "flyerPrincipal"}, allEntries = true)
    public void alternarPublicacionFlyer(Long id, boolean publicado) {
        Curso curso = cursoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Curso no encontrado"));
        if (publicado) {
            List<Curso> anteriores = cursoRepository.findByFlyerPublicadoTrueAndFlyerUrlIsNotNullOrderByUpdatedAtDesc();
            for (Curso ant : anteriores) {
                if (!ant.getId().equals(id)) {
                    ant.setFlyerPublicado(false);
                    cursoRepository.save(ant);
                }
            }
        }
        curso.setFlyerPublicado(publicado);
        cursoRepository.save(curso);
    }

    @Transactional
    @CacheEvict(value = {"cursosPublicados", "cursosDestacados", "flyerPrincipal"}, allEntries = true)
    public void cambiarEstado(Long id, EstadoCurso nuevoEstado) {
        Curso curso = cursoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Curso no encontrado"));
        curso.setEstado(nuevoEstado);
        cursoRepository.save(curso);
    }

    @Transactional
    @CacheEvict(value = {"cursosPublicados", "cursosDestacados", "flyerPrincipal"}, allEntries = true)
    public void eliminar(Long id) {
        cursoRepository.deleteById(id);
    }
}
