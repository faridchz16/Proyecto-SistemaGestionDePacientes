package com.hospital.controller;

import com.hospital.dto.CitaRequest;
import com.hospital.dto.CitaResponse;
import com.hospital.dto.MedicoResponse;
import com.hospital.model.EstadoCita;
import com.hospital.service.CitaService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/citas")
@PreAuthorize("hasAuthority('CITAS')")
public class CitaController {

    private final CitaService citaService;

    public CitaController(CitaService citaService) {
        this.citaService = citaService;
    }

    @GetMapping
    public List<CitaResponse> listar(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
                                     @RequestParam(required = false) EstadoCita estado,
                                     @RequestParam(required = false) Long medicoId) {
        return citaService.buscar(fecha, estado, medicoId).stream().map(CitaResponse::de).toList();
    }

    @GetMapping("/medicos")
    public List<MedicoResponse> medicos() {
        return citaService.medicosDisponibles().stream().map(MedicoResponse::de).toList();
    }

    @PostMapping
    public ResponseEntity<CitaResponse> registrar(@Valid @RequestBody CitaRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(CitaResponse.de(citaService.registrar(req)));
    }

    @PutMapping("/{id}")
    public CitaResponse actualizar(@PathVariable Long id, @Valid @RequestBody CitaRequest req) {
        return CitaResponse.de(citaService.actualizar(id, req));
    }

    @PatchMapping("/{id}/estado")
    public CitaResponse cambiarEstado(@PathVariable Long id, @RequestParam EstadoCita estado) {
        return CitaResponse.de(citaService.cambiarEstado(id, estado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        citaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
