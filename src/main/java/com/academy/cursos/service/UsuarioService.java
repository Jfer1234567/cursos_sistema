package com.academy.cursos.service;

import com.academy.cursos.dto.RegistroDTO;
import com.academy.cursos.model.Usuario;
import com.academy.cursos.model.enums.Rol;
import com.academy.cursos.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        Usuario usuario = new Usuario();
        usuario.setNombreCompleto(dto.getNombreCompleto());
        usuario.setTipoDocumento(dto.getTipoDocumento());
        usuario.setNumeroDocumento(dto.getNumeroDocumento());
        usuario.setCorreo(dto.getCorreo());
        usuario.setTelefono(dto.getTelefono());
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
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        usuario.setNombreCompleto(nombreCompleto);
        usuario.setTelefono(telefono);
        usuario.setInstitucionProcedencia(institucion);
        return usuarioRepository.save(usuario);
    }
}
