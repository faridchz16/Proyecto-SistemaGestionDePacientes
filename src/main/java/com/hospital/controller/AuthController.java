package com.hospital.controller;

import com.hospital.dto.PermisoResponse;
import com.hospital.dto.SesionResponse;
import com.hospital.model.Usuario;
import com.hospital.security.RedireccionPorRolHandler;
import com.hospital.service.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;

@RestController
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /** Usuario en sesión + módulos permitidos: el frontend arma el menú con esto. */
    @GetMapping("/api/auth/me")
    public SesionResponse me(Authentication auth) {
        Usuario u = usuarioService.obtenerPorUsername(auth.getName());
        return new SesionResponse(u.getId(), u.getUsername(), u.getNombreCompleto(), u.getRol().getNombre(),
                RedireccionPorRolHandler.paginaInicio(u),
                u.getRol().permisosOrdenados().stream().map(PermisoResponse::de).toList());
    }

    /** La raíz lleva a la página de inicio del rol. */
    @GetMapping("/")
    public ResponseEntity<Void> inicio(Authentication auth) {
        String destino = RedireccionPorRolHandler.paginaInicio(usuarioService.obtenerPorUsername(auth.getName()));
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(destino)).build();
    }
}
