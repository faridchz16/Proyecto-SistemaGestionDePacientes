package com.hospital.dto;

import com.hospital.model.Cita;
import com.hospital.model.EstadoCita;

import java.time.LocalDateTime;

public record CitaResponse(Long id, LocalDateTime fechaHora, String especialidad, String motivo, EstadoCita estado,
                           Long pacienteId, String pacienteDocumento, String pacienteNombre,
                           Long medicoId, String medicoNombre,
                           String creadoPor, LocalDateTime fechaCreacion) {

    public static CitaResponse de(Cita c) {
        return new CitaResponse(c.getId(), c.getFechaHora(), c.getEspecialidad(), c.getMotivo(), c.getEstado(),
                c.getPaciente().getId(), c.getPaciente().getNumeroDocumento(), c.getPaciente().getNombreCompleto(),
                c.getMedico().getId(), c.getMedico().getNombreCompleto(),
                c.getCreadoPor(), c.getFechaCreacion());
    }
}
