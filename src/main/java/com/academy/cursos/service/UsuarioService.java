package com.academy.cursos.service;

import com.academy.cursos.dto.RegistroDTO;
import com.academy.cursos.model.Usuario;
import com.academy.cursos.model.enums.Rol;
import com.academy.cursos.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final CorreoService correoService;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, CorreoService correoService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.correoService = correoService;
    }

    @Transactional
    public Usuario registrar(RegistroDTO dto) {
        if (usuarioRepository.existsByCorreo(dto.getCorreo())) {
            throw new IllegalArgumentException("Ya existe una cuenta registrada con este correo electrónico");
        }
        if (usuarioRepository.existsByNumeroDocumento(dto.getNumeroDocumento())) {
            throw new IllegalArgumentException("Ya existe una cuenta registrada con este número de documento");
        }

        if (dto.getTipoDocumento() == com.academy.cursos.model.enums.TipoDocumento.DNI) {
            if (dto.getNumeroDocumento() == null || !dto.getNumeroDocumento().trim().matches("^[0-9]{8}$")) {
                throw new IllegalArgumentException("El DNI debe contener exactamente 8 dígitos numéricos");
            }
        } else {
            if (dto.getNumeroDocumento() == null || !dto.getNumeroDocumento().trim().matches("^[A-Za-z0-9]{6,12}$")) {
                throw new IllegalArgumentException("El número de documento debe contener entre 6 y 12 caracteres alfanuméricos");
            }
        }

        if (dto.getTelefono() == null || !dto.getTelefono().trim().matches("^[0-9]{9}$")) {
            throw new IllegalArgumentException("El teléfono/WhatsApp debe contener exactamente 9 dígitos numéricos");
        }

        Usuario usuario = new Usuario();
        usuario.setNombreCompleto(dto.getNombreCompleto().trim());
        usuario.setTipoDocumento(dto.getTipoDocumento());
        usuario.setNumeroDocumento(dto.getNumeroDocumento().trim());
        usuario.setCorreo(dto.getCorreo().trim());
        usuario.setTelefono(dto.getTelefono().trim());
        usuario.setInstitucionProcedencia(dto.getInstitucionProcedencia());
        usuario.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        usuario.setRol(Rol.PARTICIPANTE);
        usuario.setActivo(true);

        Usuario guardado = usuarioRepository.save(usuario);
        correoService.enviarBienvenida(guardado);
        return guardado;
    }

    public Optional<Usuario> buscarPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    @Transactional
    public Usuario actualizarPerfil(Long id, String nombreCompleto, String telefono, String institucion) {
        if (telefono == null || !telefono.trim().matches("^[0-9]{9}$")) {
            throw new IllegalArgumentException("El teléfono/WhatsApp debe contener exactamente 9 dígitos numéricos");
        }

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        usuario.setNombreCompleto(nombreCompleto.trim());
        usuario.setTelefono(telefono.trim());
        usuario.setInstitucionProcedencia(institucion != null ? institucion.trim() : null);
        return usuarioRepository.save(usuario);
    }

    public long contarParticipantes() {
        return usuarioRepository.countByRol(Rol.PARTICIPANTE);
    }

    public List<Usuario> listarParticipantes(String filtro) {
        if (filtro != null && !filtro.trim().isEmpty()) {
            return usuarioRepository.buscarParticipantes(Rol.PARTICIPANTE, filtro.trim());
        }
        return usuarioRepository.findByRolOrderByCreatedAtDesc(Rol.PARTICIPANTE);
    }
}
