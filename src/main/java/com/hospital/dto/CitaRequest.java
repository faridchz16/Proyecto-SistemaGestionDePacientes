package com.hospital.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record CitaRequest(
        @NotNull(message = "Debe seleccionar un paciente") Long pacienteId,
        @NotNull(message = "Debe seleccionar un médico") Long medicoId,
        @NotNull(message = "La fecha y hora son obligatorias") LocalDateTime fechaHora,
        @NotBlank(message = "La especialidad es obligatoria") @Size(max = 80) String especialidad,
        @Size(max = 500) String motivo) {
}
