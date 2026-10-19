package com.academy.cursos.service;

import com.academy.cursos.model.AreaInvestigacion;
import com.academy.cursos.model.LineaInvestigacion;
import com.academy.cursos.repository.AreaInvestigacionRepository;
import com.academy.cursos.repository.LineaInvestigacionRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class InvestigacionService {

    private final LineaInvestigacionRepository lineaRepo;
    private final AreaInvestigacionRepository areaRepo;

    public InvestigacionService(
            LineaInvestigacionRepository lineaRepo,
            AreaInvestigacionRepository areaRepo) {
        this.lineaRepo = lineaRepo;
        this.areaRepo = areaRepo;
    }

    @Cacheable("lineasInvestigacion")
    public List<LineaInvestigacion> listarLineas() {
        return lineaRepo.findAllByOrderByOrdenAsc();
    }

    @Cacheable("areasInvestigacion")
    public List<AreaInvestigacion> listarAreas() {
        return areaRepo.findAllConLinea();
    }

    @CacheEvict(value = {"lineasInvestigacion", "areasInvestigacion"}, allEntries = true)
    public void limpiarCache() {
        // Método disponible para invalidar la memoria si se modifican áreas o líneas
    }
}
