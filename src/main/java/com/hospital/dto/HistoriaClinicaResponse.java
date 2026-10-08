package com.hospital.dto;

import com.hospital.model.HistoriaClinica;
import com.hospital.model.Paciente;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record HistoriaClinicaResponse(Long id, String numeroHistoria, LocalDate fechaApertura,
                                      Long pacienteId, String pacienteDocumento, String pacienteNombre,
                                      String grupoSanguineo, String alergias, String antecedentesPersonales,
                                      String antecedentesFamiliares, String observaciones,
                                      String creadoPor, LocalDateTime fechaCreacion,
                                      String modificadoPor, LocalDateTime fechaModificacion) {

    public static HistoriaClinicaResponse de(HistoriaClinica h) {
        Paciente p = h.getPaciente();
        return new HistoriaClinicaResponse(h.getId(), h.getNumeroHistoria(), h.getFechaApertura(),
                p.getId(), p.getNumeroDocumento(), p.getNombreCompleto(),
                h.getGrupoSanguineo(), h.getAlergias(), h.getAntecedentesPersonales(),
                h.getAntecedentesFamiliares(), h.getObservaciones(),
                h.getCreadoPor(), h.getFechaCreacion(), h.getModificadoPor(), h.getFechaModificacion());
    }
}
