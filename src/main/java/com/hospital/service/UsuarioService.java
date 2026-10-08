package com.hospital.service;

import com.hospital.audit.Auditable;
import com.hospital.dto.UsuarioRequest;
import com.hospital.exception.RecursoNoEncontradoException;
import com.hospital.exception.ReglaNegocioException;
import com.hospital.model.Operacion;
import com.hospital.model.Rol;
import com.hospital.model.Usuario;
import com.hospital.repository.UsuarioRepository;
import com.hospital.security.SecurityUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolService rolService;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, RolService rolService, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolService = rolService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<Usuario> listar() {
        return usuarioRepository.findAllByOrderByIdAsc();
    }

    @Transactional(readOnly = true)
    public Usuario obtener(Long id) {
        return usuarioRepository.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("el usuario", id));
    }

    @Transactional(readOnly = true)
    public Usuario obtenerPorUsername(String username) {
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RecursoNoEncontradoException("el usuario", username));
    }

    @Transactional
    @Auditable(entidad = "Usuario", operacion = Operacion.REGISTRAR)
    public Usuario registrar(UsuarioRequest req) {
        String username = req.username().trim().toLowerCase();
        String email = req.email().trim().toLowerCase();
        if (usuarioRepository.existsByUsername(username)) {
            throw new ReglaNegocioException("El nombre de usuario '" + username + "' ya existe");
        }
        if (usuarioRepository.existsByEmail(email)) {
            throw new ReglaNegocioException("El correo " + email + " ya está registrado");
        }
        validarPassword(req.password(), true);

        Usuario u = new Usuario();
        u.setUsername(username);
        u.setEmail(email);
        u.setNombres(req.nombres().trim());
        u.setApellidos(req.apellidos().trim());
        u.setPassword(passwordEncoder.encode(req.password()));
        u.setRol(rolActivo(req.rolId()));
        u.setActivo(true);
        return usuarioRepository.save(u);
    }

    @Transactional
    @Auditable(entidad = "Usuario", operacion = Operacion.MODIFICAR)
    public Usuario actualizar(Long id, UsuarioRequest req) {
        Usuario u = obtener(id);
        String username = req.username().trim().toLowerCase();
        String email = req.email().trim().toLowerCase();
        if (usuarioRepository.existsByUsernameAndIdNot(username, id)) {
            throw new ReglaNegocioException("El nombre de usuario '" + username + "' ya existe");
        }
        if (usuarioRepository.existsByEmailAndIdNot(email, id)) {
            throw new ReglaNegocioException("El correo " + email + " ya está registrado");
        }
        Rol nuevoRol = u.getRol().getId().equals(req.rolId()) ? u.getRol() : rolActivo(req.rolId());
        if (esUsuarioActual(u) && !nuevoRol.getId().equals(u.getRol().getId())) {
            throw new ReglaNegocioException("No puede cambiar su propio rol");
        }
        if (esUsuarioActual(u) && !u.getUsername().equals(username)) {
            throw new ReglaNegocioException("No puede cambiar su propio nombre de usuario mientras tiene la sesión iniciada");
        }
        if (req.password() != null && !req.password().isBlank()) {
            validarPassword(req.password(), false);
            u.setPassword(passwordEncoder.encode(req.password()));
        }
        u.setUsername(username);
        u.setEmail(email);
        u.setNombres(req.nombres().trim());
        u.setApellidos(req.apellidos().trim());
        u.setRol(nuevoRol);
        return u;
    }

    @Transactional
    @Auditable(entidad = "Usuario", cambioEstado = true)
    public Usuario cambiarEstado(Long id, boolean activo) {
        Usuario u = obtener(id);
        if (!activo && esUsuarioActual(u)) {
            throw new ReglaNegocioException("No puede desactivar su propio usuario");
        }
        if (activo && !u.getRol().isActivo()) {
            throw new ReglaNegocioException("No se puede activar: el rol " + u.getRol().getNombre() + " está inactivo");
        }
        u.setActivo(activo);
        return u;
    }

    private Rol rolActivo(Long rolId) {
        Rol rol = rolService.obtener(rolId);
        if (!rol.isActivo()) {
            throw new ReglaNegocioException("El rol " + rol.getNombre() + " está inactivo y no puede asignarse");
        }
        return rol;
    }

    private boolean esUsuarioActual(Usuario u) {
        return u.getUsername().equals(SecurityUtils.usuarioActual());
    }

    /** Política mínima: 8 caracteres, al menos una letra y un número. */
    private void validarPassword(String password, boolean obligatoria) {
        if (password == null || password.isBlank()) {
            if (obligatoria) throw new ReglaNegocioException("La contraseña es obligatoria");
            return;
        }
        if (password.length() < 8 || !password.matches(".*[A-Za-z].*") || !password.matches(".*\\d.*")) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres, una letra y un número");
        }
    }
}
