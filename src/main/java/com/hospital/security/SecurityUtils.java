package com.hospital.security;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    public static final String SISTEMA = "SISTEMA";

    private SecurityUtils() { }

    /** Username del usuario autenticado, o "SISTEMA" para procesos internos (carga inicial). */
    public static String usuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return SISTEMA;
        }
        return auth.getName();
    }
}
