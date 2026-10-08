package com.hospital.dto;

import com.hospital.model.Usuario;

public record MedicoResponse(Long id, String nombreCompleto) {
    public static MedicoResponse de(Usuario u) {
        return new MedicoResponse(u.getId(), u.getNombreCompleto());
    }
}
