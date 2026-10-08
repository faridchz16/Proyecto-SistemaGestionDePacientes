package com.hospital.service;

import com.hospital.audit.Auditable;
import com.hospital.exception.RecursoNoEncontradoException;
import com.hospital.exception.ReglaNegocioException;
import com.hospital.model.AtencionResumen;
import com.hospital.model.ContactoEmergencia;
import com.hospital.model.Operacion;
import com.hospital.model.Paciente;
import com.hospital.repository.AtencionResumenRepository;
import com.hospital.repository.PacienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PacienteService {

    public static final String ACTIVO = "ACTIVO";
    public static final String INACTIVO = "INACTIVO";

    private final PacienteRepository pacienteRepository;
    private final AtencionResumenRepository atencionRepository;

    public PacienteService(PacienteRepository pacienteRepository, AtencionResumenRepository atencionRepository) {
        this.pacienteRepository = pacienteRepository;
        this.atencionRepository = atencionRepository;
    }

    @Transactional(readOnly = true)
    public List<Paciente> buscar(String q) {
        return pacienteRepository.buscar(q == null ? null : q.trim());
    }

    @Transactional(readOnly = true)
    public Paciente obtenerPorId(Long id) {
        return pacienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("el paciente", id));
    }

    @Transactional
    @Auditable(entidad = "Paciente", operacion = Operacion.REGISTRAR)
    public Paciente registrar(Paciente paciente) {
        if (pacienteRepository.existsByNumeroDocumento(paciente.getNumeroDocumento())) {
            throw new ReglaNegocioException("El documento " + paciente.getNumeroDocumento() + " ya se encuentra registrado");
        }
        paciente.setId(null);
        paciente.setEstado(ACTIVO);
        if (paciente.getContactosEmergencia() == null) {
            paciente.setContactosEmergencia(new java.util.ArrayList<>());
        }
        paciente.getContactosEmergencia().forEach(c -> {
            c.setId(null);
            c.setPaciente(paciente);
        });
        return pacienteRepository.save(paciente);
    }

    @Transactional
    @Auditable(entidad = "Paciente", operacion = Operacion.MODIFICAR)
    public Paciente actualizar(Long id, Paciente datos) {
        Paciente paciente = obtenerPorId(id);
        if (pacienteRepository.existsByNumeroDocumentoAndIdNot(datos.getNumeroDocumento(), id)) {
            throw new ReglaNegocioException("El documento " + datos.getNumeroDocumento() + " pertenece a otro paciente");
        }
        paciente.setTipoDocumento(datos.getTipoDocumento());
        paciente.setNumeroDocumento(datos.getNumeroDocumento());
        paciente.setNombres(datos.getNombres());
        paciente.setApellidoPaterno(datos.getApellidoPaterno());
        paciente.setApellidoMaterno(datos.getApellidoMaterno());
        paciente.setFechaNacimiento(datos.getFechaNacimiento());
        paciente.setSexo(datos.getSexo());
        paciente.setTelefono(datos.getTelefono());
        paciente.setCorreo(datos.getCorreo());
        paciente.setDireccion(datos.getDireccion());

        // orphanRemoval = true: los contactos que ya no vienen se eliminan de la BD
        paciente.getContactosEmergencia().clear();
        List<ContactoEmergencia> nuevos = datos.getContactosEmergencia() == null ? List.of() : datos.getContactosEmergencia();
        for (ContactoEmergencia c : nuevos) {
            c.setId(null);
            c.setPaciente(paciente);
            paciente.getContactosEmergencia().add(c);
        }
        return pacienteRepository.save(paciente);
    }

    /** Eliminación lógica: el paciente no se borra porque tiene historia, citas y atenciones asociadas. */
    @Transactional
    @Auditable(entidad = "Paciente", cambioEstado = true)
    public Paciente cambiarEstado(Long id, boolean activo) {
        Paciente paciente = obtenerPorId(id);
        paciente.setEstado(activo ? ACTIVO : INACTIVO);
        return paciente;
    }

    // ---------- Atenciones (1 paciente -> N atenciones) ----------

    @Transactional(readOnly = true)
    public List<AtencionResumen> obtenerAtenciones(Long pacienteId) {
        obtenerPorId(pacienteId);
        return atencionRepository.findByPacienteIdOrderByFechaAtencionDesc(pacienteId);
    }

    @Transactional
    @Auditable(entidad = "AtencionResumen", operacion = Operacion.REGISTRAR)
    public AtencionResumen registrarAtencion(Long pacienteId, AtencionResumen atencion) {
        Paciente paciente = obtenerPorId(pacienteId);
        if (INACTIVO.equals(paciente.getEstado())) {
            throw new ReglaNegocioException("No se pueden registrar atenciones a un paciente inactivo");
        }
        atencion.setId(null);
        atencion.setPaciente(paciente);
        return atencionRepository.save(atencion);
    }

    @Transactional
    @Auditable(entidad = "AtencionResumen", operacion = Operacion.MODIFICAR)
    public AtencionResumen actualizarAtencion(Long pacienteId, Long atencionId, AtencionResumen datos) {
        AtencionResumen atencion = atencionRepository.findByIdAndPacienteId(atencionId, pacienteId)
                .orElseThrow(() -> new RecursoNoEncontradoException("la atención del paciente", atencionId));
        atencion.setFechaAtencion(datos.getFechaAtencion());
        atencion.setEspecialidad(datos.getEspecialidad());
        atencion.setMedico(datos.getMedico());
        atencion.setDiagnostico(datos.getDiagnostico());
        return atencion;
    }

    @Transactional
    @Auditable(entidad = "AtencionResumen", operacion = Operacion.ELIMINAR)
    public AtencionResumen eliminarAtencion(Long pacienteId, Long atencionId) {
        AtencionResumen atencion = atencionRepository.findByIdAndPacienteId(atencionId, pacienteId)
                .orElseThrow(() -> new RecursoNoEncontradoException("la atención del paciente", atencionId));
        atencionRepository.delete(atencion);
        return atencion;
    }
}
