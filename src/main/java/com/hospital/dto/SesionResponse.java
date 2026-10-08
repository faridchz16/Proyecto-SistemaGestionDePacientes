package com.hospital.dto;

import java.util.List;

/** Datos del usuario autenticado que el frontend usa para armar el menú. */
public record SesionResponse(Long id, String username, String nombreCompleto, String rol,
                             String paginaInicio, List<PermisoResponse> modulos) {
}
