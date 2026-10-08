package com.hospital.service;

import com.hospital.audit.Auditable;
import com.hospital.dto.HistoriaClinicaRequest;
import com.hospital.exception.RecursoNoEncontradoException;
import com.hospital.exception.ReglaNegocioException;
import com.hospital.model.HistoriaClinica;
import com.hospital.model.Operacion;
import com.hospital.model.Paciente;
import com.hospital.repository.HistoriaClinicaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class HistoriaClinicaService {

    private final HistoriaClinicaRepository historiaRepository;
    private final PacienteService pacienteService;

    public HistoriaClinicaService(HistoriaClinicaRepository historiaRepository, PacienteService pacienteService) {
        this.historiaRepository = historiaRepository;
        this.pacienteService = pacienteService;
    }

    @Transactional(readOnly = true)
    public Optional<HistoriaClinica> buscarPorPaciente(Long pacienteId) {
        pacienteService.obtenerPorId(pacienteId);
        return historiaRepository.findByPacienteId(pacienteId);
    }

    @Transactional
    @Auditable(entidad = "HistoriaClinica", operacion = Operacion.REGISTRAR)
    public HistoriaClinica aperturar(Long pacienteId, HistoriaClinicaRequest req) {
        Paciente paciente = pacienteService.obtenerPorId(pacienteId);
        if (historiaRepository.existsByPacienteId(pacienteId)) {
            throw new ReglaNegocioException("El paciente ya tiene una historia clínica aperturada");
        }
        HistoriaClinica h = new HistoriaClinica();
        h.setPaciente(paciente);
        h.setFechaApertura(LocalDate.now());
        // Número de historia legible y único: HC-<año>-<id paciente con 6 dígitos>
        h.setNumeroHistoria(String.format("HC-%d-%06d", LocalDate.now().getYear(), pacienteId));
        copiar(req, h);
        return historiaRepository.save(h);
    }

    @Transactional
    @Auditable(entidad = "HistoriaClinica", operacion = Operacion.MODIFICAR)
    public HistoriaClinica actualizar(Long pacienteId, HistoriaClinicaRequest req) {
        HistoriaClinica h = historiaRepository.findByPacienteId(pacienteId)
                .orElseThrow(() -> new RecursoNoEncontradoException("historia clínica para el paciente", pacienteId));
        copiar(req, h);
        return h;
    }

    private void copiar(HistoriaClinicaRequest req, HistoriaClinica h) {
        h.setGrupoSanguineo(req.grupoSanguineo());
        h.setAlergias(req.alergias());
        h.setAntecedentesPersonales(req.antecedentesPersonales());
        h.setAntecedentesFamiliares(req.antecedentesFamiliares());
        h.setObservaciones(req.observaciones());
    }
}
