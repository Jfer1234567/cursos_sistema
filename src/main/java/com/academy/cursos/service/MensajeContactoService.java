package com.academy.cursos.service;

import com.academy.cursos.dto.ContactoDTO;
import com.academy.cursos.model.MensajeContacto;
import com.academy.cursos.repository.MensajeContactoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MensajeContactoService {

    private final MensajeContactoRepository mensajeRepo;
    private final CorreoService correoService;

    public MensajeContactoService(MensajeContactoRepository mensajeRepo, CorreoService correoService) {
        this.mensajeRepo = mensajeRepo;
        this.correoService = correoService;
    }

    @Transactional
    public MensajeContacto guardarMensaje(ContactoDTO dto) {
        MensajeContacto mensaje = new MensajeContacto();
        mensaje.setNombreCompleto(dto.getNombreCompleto().trim());
        mensaje.setCorreo(dto.getCorreo().trim());
        mensaje.setTelefono(dto.getTelefono() != null ? dto.getTelefono().trim() : null);
        mensaje.setAsunto(dto.getAsunto().trim());
        mensaje.setMensaje(dto.getMensaje().trim());
        mensaje.setLeido(false);
        mensaje.setRespondido(false);

        MensajeContacto guardado = mensajeRepo.save(mensaje);

        // Notificación de respaldo al correo institucional
        try {
            correoService.enviarNotificacionContacto(guardado);
        } catch (Exception ignored) {
            // Asegurar que si falla el correo, el mensaje permanezca guardado en la base de datos
        }

        return guardado;
    }

    public List<MensajeContacto> listarMensajes(String filtro) {
        if (filtro != null && !filtro.trim().isEmpty()) {
            return mensajeRepo.buscarMensajes(filtro.trim());
        }
        return mensajeRepo.findAllByOrderByFechaEnvioDesc();
    }

    public long contarNoLeidos() {
        return mensajeRepo.countByLeidoFalse();
    }

    public Optional<MensajeContacto> obtenerPorId(Long id) {
        return mensajeRepo.findById(id);
    }

    @Transactional
    public void marcarComoLeido(Long id) {
        mensajeRepo.findById(id).ifPresent(m -> {
            m.setLeido(true);
            mensajeRepo.save(m);
        });
    }

    @Transactional
    public void marcarComoRespondido(Long id) {
        mensajeRepo.findById(id).ifPresent(m -> {
            m.setRespondido(true);
            m.setLeido(true);
            mensajeRepo.save(m);
        });
    }

    @Transactional
    public void eliminarMensaje(Long id) {
        mensajeRepo.deleteById(id);
    }
}
