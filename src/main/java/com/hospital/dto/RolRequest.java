package com.hospital.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record RolRequest(
        @NotBlank(message = "El nombre del rol es obligatorio")
        @Size(min = 3, max = 40, message = "El nombre debe tener entre 3 y 40 caracteres")
        @Pattern(regexp = "^[A-Za-zÁÉÍÓÚÑáéíóúñ_ ]+$", message = "Solo letras, espacios y guion bajo")
        String nombre,

        @Size(max = 200) String descripcion,

        Set<Long> permisoIds) {
}
