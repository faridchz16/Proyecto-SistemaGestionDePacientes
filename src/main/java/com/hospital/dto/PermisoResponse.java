package com.hospital.dto;

import com.hospital.model.Permiso;

public record PermisoResponse(Long id, String codigo, String nombre, String ruta, String icono, Integer orden) {
    public static PermisoResponse de(Permiso p) {
        return new PermisoResponse(p.getId(), p.getCodigo(), p.getNombre(), p.getRuta(), p.getIcono(), p.getOrden());
    }
}
