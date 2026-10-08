package com.hospital.controller;

import com.hospital.dto.HistoriaClinicaRequest;
import com.hospital.dto.HistoriaClinicaResponse;
import com.hospital.service.HistoriaClinicaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/historias")
@PreAuthorize("hasAuthority('HISTORIAS')")
public class HistoriaClinicaController {

    private final HistoriaClinicaService historiaService;

    public HistoriaClinicaController(HistoriaClinicaService historiaService) {
        this.historiaService = historiaService;
    }

    /** 200 con la historia, o 204 si el paciente aún no tiene historia aperturada. */
    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<HistoriaClinicaResponse> porPaciente(@PathVariable Long pacienteId) {
        return historiaService.buscarPorPaciente(pacienteId)
                .map(h -> ResponseEntity.ok(HistoriaClinicaResponse.de(h)))
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PostMapping("/paciente/{pacienteId}")
    public ResponseEntity<HistoriaClinicaResponse> aperturar(@PathVariable Long pacienteId,
                                                             @Valid @RequestBody HistoriaClinicaRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(HistoriaClinicaResponse.de(historiaService.aperturar(pacienteId, req)));
    }

    @PutMapping("/paciente/{pacienteId}")
    public HistoriaClinicaResponse actualizar(@PathVariable Long pacienteId,
                                              @Valid @RequestBody HistoriaClinicaRequest req) {
        return HistoriaClinicaResponse.de(historiaService.actualizar(pacienteId, req));
    }
}
