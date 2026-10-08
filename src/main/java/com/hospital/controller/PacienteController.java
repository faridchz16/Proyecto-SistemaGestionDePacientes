package com.hospital.controller;

import com.hospital.model.AtencionResumen;
import com.hospital.model.Paciente;
import com.hospital.service.PacienteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

    private final PacienteService pacienteService;

    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    /** Búsqueda por documento o nombre (q opcional). La usan Pacientes, Historias y Citas. */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('PACIENTES','HISTORIAS','CITAS')")
    public List<Paciente> buscar(@RequestParam(required = false) String q) {
        return pacienteService.buscar(q);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('PACIENTES','HISTORIAS','CITAS')")
    public Paciente obtener(@PathVariable Long id) {
        return pacienteService.obtenerPorId(id);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PACIENTES')")
    public ResponseEntity<Paciente> crear(@Valid @RequestBody Paciente paciente) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pacienteService.registrar(paciente));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PACIENTES')")
    public Paciente actualizar(@PathVariable Long id, @Valid @RequestBody Paciente datos) {
        return pacienteService.actualizar(id, datos);
    }

    /** Activar / desactivar (eliminación lógica). */
    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAuthority('PACIENTES')")
    public Paciente cambiarEstado(@PathVariable Long id, @RequestParam boolean activo) {
        return pacienteService.cambiarEstado(id, activo);
    }

    // ---------- Atenciones: solo personal con acceso a historias clínicas ----------

    @GetMapping("/{id}/atenciones")
    @PreAuthorize("hasAuthority('HISTORIAS')")
    public List<AtencionResumen> atenciones(@PathVariable Long id) {
        return pacienteService.obtenerAtenciones(id);
    }

    @PostMapping("/{id}/atenciones")
    @PreAuthorize("hasAuthority('HISTORIAS')")
    public ResponseEntity<AtencionResumen> registrarAtencion(@PathVariable Long id,
                                                            @Valid @RequestBody AtencionResumen atencion) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pacienteService.registrarAtencion(id, atencion));
    }

    @PutMapping("/{id}/atenciones/{atencionId}")
    @PreAuthorize("hasAuthority('HISTORIAS')")
    public AtencionResumen actualizarAtencion(@PathVariable Long id, @PathVariable Long atencionId,
                                              @Valid @RequestBody AtencionResumen atencion) {
        return pacienteService.actualizarAtencion(id, atencionId, atencion);
    }

    @DeleteMapping("/{id}/atenciones/{atencionId}")
    @PreAuthorize("hasAuthority('HISTORIAS')")
    public ResponseEntity<Void> eliminarAtencion(@PathVariable Long id, @PathVariable Long atencionId) {
        pacienteService.eliminarAtencion(id, atencionId);
        return ResponseEntity.noContent().build();
    }
}
