package com.hospital.dto;

import com.hospital.model.Rol;

import java.time.LocalDateTime;
import java.util.List;

public record RolResponse(Long id, String nombre, String descripcion, boolean activo,
                          List<PermisoResponse> permisos, long cantidadUsuarios,
                          String creadoPor, LocalDateTime fechaCreacion,
                          String modificadoPor, LocalDateTime fechaModificacion) {

    public static RolResponse de(Rol r, long cantidadUsuarios) {
        return new RolResponse(r.getId(), r.getNombre(), r.getDescripcion(), r.isActivo(),
                r.permisosOrdenados().stream().map(PermisoResponse::de).toList(), cantidadUsuarios,
                r.getCreadoPor(), r.getFechaCreacion(), r.getModificadoPor(), r.getFechaModificacion());
    }
}
