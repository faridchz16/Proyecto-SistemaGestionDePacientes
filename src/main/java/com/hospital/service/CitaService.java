package com.hospital.service;

import com.hospital.audit.Auditable;
import com.hospital.dto.CitaRequest;
import com.hospital.exception.RecursoNoEncontradoException;
import com.hospital.exception.ReglaNegocioException;
import com.hospital.model.*;
import com.hospital.repository.CitaRepository;
import com.hospital.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CitaService {

    private final CitaRepository citaRepository;
    private final UsuarioRepository usuarioRepository;
    private final PacienteService pacienteService;

    public CitaService(CitaRepository citaRepository, UsuarioRepository usuarioRepository, PacienteService pacienteService) {
        this.citaRepository = citaRepository;
        this.usuarioRepository = usuarioRepository;
        this.pacienteService = pacienteService;
    }

    @Transactional(readOnly = true)
    public List<Cita> buscar(LocalDate fecha, EstadoCita estado, Long medicoId) {
        LocalDateTime desde = fecha != null ? fecha.atStartOfDay() : null;
        LocalDateTime hasta = fecha != null ? fecha.plusDays(1).atStartOfDay() : null;
        return citaRepository.buscar(desde, hasta, estado, medicoId);
    }

    @Transactional(readOnly = true)
    public List<Cita> porPaciente(Long pacienteId) {
        return citaRepository.findByPacienteIdOrderByFechaHoraDesc(pacienteId);
    }

    @Transactional(readOnly = true)
    public List<Usuario> medicosDisponibles() {
        return usuarioRepository.findByRolNombreAndActivoTrueOrderByApellidos(Rol.MEDICO);
    }

    @Transactional
    @Auditable(entidad = "Cita", operacion = Operacion.REGISTRAR)
    public Cita registrar(CitaRequest req) {
        Cita cita = new Cita();
        if (req.fechaHora().isBefore(LocalDateTime.now())) {
            throw new ReglaNegocioException("La cita debe programarse en una fecha y hora futura");
        }
        asignar(cita, req, 0L);
        return citaRepository.save(cita);
    }

    @Transactional
    @Auditable(entidad = "Cita", operacion = Operacion.MODIFICAR)
    public Cita actualizar(Long id, CitaRequest req) {
        Cita cita = obtener(id);
        if (cita.getEstado() != EstadoCita.PROGRAMADA) {
            throw new ReglaNegocioException("Solo se pueden editar citas en estado PROGRAMADA");
        }
        asignar(cita, req, id);
        return cita;
    }

    @Transactional
    @Auditable(entidad = "Cita", operacion = Operacion.MODIFICAR)
    public Cita cambiarEstado(Long id, EstadoCita nuevoEstado) {
        Cita cita = obtener(id);
        if (cita.getEstado() != EstadoCita.PROGRAMADA) {
            throw new ReglaNegocioException("La cita ya se encuentra " + cita.getEstado());
        }
        cita.setEstado(nuevoEstado);
        return cita;
    }

    @Transactional
    @Auditable(entidad = "Cita", operacion = Operacion.ELIMINAR)
    public Cita eliminar(Long id) {
        Cita cita = obtener(id);
        if (cita.getEstado() == EstadoCita.ATENDIDA) {
            throw new ReglaNegocioException("No se puede eliminar una cita ya atendida");
        }
        citaRepository.delete(cita);
        return cita;
    }

    @Transactional(readOnly = true)
    public Cita obtener(Long id) {
        return citaRepository.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("la cita", id));
    }

    private void asignar(Cita cita, CitaRequest req, Long idActual) {
        Paciente paciente = pacienteService.obtenerPorId(req.pacienteId());
        if (PacienteService.INACTIVO.equals(paciente.getEstado())) {
            throw new ReglaNegocioException("El paciente está inactivo");
        }
        Usuario medico = usuarioRepository.findById(req.medicoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("el médico", req.medicoId()));
        if (!Rol.MEDICO.equals(medico.getRol().getNombre()) || !medico.isActivo()) {
            throw new ReglaNegocioException("El usuario seleccionado no es un médico activo");
        }
        LocalDateTime fechaHora = req.fechaHora().withSecond(0).withNano(0);
        if (citaRepository.existsByMedicoIdAndFechaHoraAndEstadoNotAndIdNot(
                medico.getId(), fechaHora, EstadoCita.CANCELADA, idActual)) {
            throw new ReglaNegocioException("El médico ya tiene una cita programada en ese horario");
        }
        cita.setPaciente(paciente);
        cita.setMedico(medico);
        cita.setFechaHora(fechaHora);
        cita.setEspecialidad(req.especialidad());
        cita.setMotivo(req.motivo());
    }
}
