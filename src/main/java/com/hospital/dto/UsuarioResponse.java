package com.hospital.dto;

import com.hospital.model.Usuario;

import java.time.LocalDateTime;

public record UsuarioResponse(Long id, String username, String nombres, String apellidos, String email,
                              boolean activo, Long rolId, String rolNombre, boolean rolActivo,
                              LocalDateTime ultimoAcceso, String creadoPor, LocalDateTime fechaCreacion,
                              String modificadoPor, LocalDateTime fechaModificacion) {

    public static UsuarioResponse de(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getUsername(), u.getNombres(), u.getApellidos(), u.getEmail(),
                u.isActivo(), u.getRol().getId(), u.getRol().getNombre(), u.getRol().isActivo(),
                u.getUltimoAcceso(), u.getCreadoPor(), u.getFechaCreacion(),
                u.getModificadoPor(), u.getFechaModificacion());
    }
}
