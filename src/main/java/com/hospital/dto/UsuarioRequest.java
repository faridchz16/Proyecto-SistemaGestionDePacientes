package com.hospital.dto;

import jakarta.validation.constraints.*;

/**
 * Datos para registrar/editar un usuario. En la edición la contraseña es opcional:
 * si llega vacía se conserva la actual.
 */
public record UsuarioRequest(
        @NotBlank(message = "El usuario es obligatorio")
        @Size(min = 4, max = 50, message = "El usuario debe tener entre 4 y 50 caracteres")
        @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Solo letras, números, punto, guion y guion bajo")
        String username,

        @Size(max = 64, message = "La contraseña no puede superar 64 caracteres")
        String password,

        @NotBlank(message = "Los nombres son obligatorios")
        @Size(max = 80) String nombres,

        @NotBlank(message = "Los apellidos son obligatorios")
        @Size(max = 80) String apellidos,

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo no tiene un formato válido")
        @Size(max = 100) String email,

        @NotNull(message = "Debe seleccionar un rol")
        Long rolId) {
}
