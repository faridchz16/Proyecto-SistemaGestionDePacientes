package com.hospital.security;

import com.hospital.model.Usuario;
import com.hospital.repository.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

/**
 * Tras un login exitoso redirige a la página de inicio del rol:
 * el primer módulo permitido según el orden del menú.
 *  ADMINISTRADOR -> usuarios.html | MEDICO -> historias.html | RECEPCIONISTA -> citas.html
 */
@Component
public class RedireccionPorRolHandler implements AuthenticationSuccessHandler {

    private final UsuarioRepository usuarioRepository;

    public RedireccionPorRolHandler(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        String destino = usuarioRepository.findByUsername(authentication.getName())
                .map(RedireccionPorRolHandler::paginaInicio)
                .orElse("/index.html");
        response.sendRedirect(request.getContextPath() + destino);
    }

    public static String paginaInicio(Usuario u) {
        return u.getRol().permisosOrdenados().stream()
                .findFirst()
                .map(p -> p.getRuta())
                .orElse("/index.html");
    }
}
