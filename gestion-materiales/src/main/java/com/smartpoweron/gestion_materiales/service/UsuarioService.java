package com.smartpoweron.gestion_materiales.service;

import com.smartpoweron.gestion_materiales.model.Rol;
import com.smartpoweron.gestion_materiales.model.Usuario;
import com.smartpoweron.gestion_materiales.repository.RolRepository;
import com.smartpoweron.gestion_materiales.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    public List<Rol> listarRoles() {
        return rolRepository.findAll();
    }

    public Usuario crearUsuario(String nombreCompleto, String correo, String password, Long idRol) {
        if (usuarioRepository.existsByCorreo(correo)) {
            throw new RuntimeException("El correo ya está registrado.");
        }
        Rol rol = rolRepository.findById(idRol)
                .orElseThrow(() -> new RuntimeException("Rol no válido."));

        Usuario usuario = new Usuario();
        usuario.setNombreCompleto(nombreCompleto);
        usuario.setCorreo(correo);
        usuario.setPasswordHash(passwordEncoder.encode(password));
        usuario.setRol(rol);
        usuario.setActivo(true);
        return usuarioRepository.save(usuario);
    }

    public Usuario cambiarEstado(Long idUsuario, Boolean activo) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));
        usuario.setActivo(activo);
        return usuarioRepository.save(usuario);
    }

    public Usuario obtenerPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado (id=" + id + ")."));
    }

    /**
     * Edición de un usuario: nombre, correo, rol y estado. La contraseña no se
     * toca desde aquí (se gestiona en un flujo aparte).
     */
    @Transactional
    public Usuario actualizarUsuario(Long id, String nombreCompleto, String correo, Long idRol, Boolean activo) {
        Usuario u = obtenerPorId(id);
        if (!Objects.equals(u.getCorreo(), correo) && usuarioRepository.existsByCorreo(correo)) {
            throw new RuntimeException("El correo ya está registrado por otro usuario.");
        }
        u.setNombreCompleto(nombreCompleto);
        u.setCorreo(correo);
        if (idRol != null) {
            Rol rol = rolRepository.findById(idRol)
                    .orElseThrow(() -> new RuntimeException("Rol no válido."));
            u.setRol(rol);
        }
        if (activo != null) {
            u.setActivo(activo);
        }
        return usuarioRepository.save(u);
    }

    @Transactional
    public void eliminarUsuario(Long id) {
        Usuario u = obtenerPorId(id);
        try {
            usuarioRepository.delete(u);
            usuarioRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException(
                "No se puede eliminar: el usuario tiene solicitudes, despachos, "
                + "pruebas de calidad o movimientos asociados. "
                + "Considere desactivar la cuenta en su lugar.");
        }
    }
}
