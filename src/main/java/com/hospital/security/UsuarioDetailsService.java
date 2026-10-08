package com.hospital.security;

import com.hospital.model.Rol;
import com.hospital.model.Usuario;
import com.hospital.repository.UsuarioRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Carga el usuario desde la BD para Spring Security.
 * Authorities generadas:
 *  - ROLE_<NOMBRE_ROL>       (p. ej. ROLE_ADMINISTRADOR)
 *  - un authority por módulo (p. ej. USUARIOS, PACIENTES, CITAS)
 * Un usuario inactivo, o cuyo rol está inactivo, no puede iniciar sesión.
 */
@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario u = usuarioRepository.findByUsername(username.trim().toLowerCase())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        Rol rol = u.getRol();

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + rol.getNombre()));
        rol.getPermisos().forEach(p -> authorities.add(new SimpleGrantedAuthority(p.getCodigo())));

        return User.withUsername(u.getUsername())
                .password(u.getPassword())
                .authorities(authorities)
                .disabled(!u.isActivo() || !rol.isActivo())
                .build();
    }
}
