package com.hospital.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record HistoriaClinicaRequest(
        @Pattern(regexp = "^$|^(A|B|AB|O)[+-]$", message = "Grupo sanguíneo inválido (ej. O+, AB-)")
        String grupoSanguineo,
        @Size(max = 500) String alergias,
        @Size(max = 1000) String antecedentesPersonales,
        @Size(max = 1000) String antecedentesFamiliares,
        @Size(max = 1000) String observaciones) {
}
