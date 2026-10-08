package com.hospital.controller;

import com.hospital.model.Auditoria;
import com.hospital.model.Operacion;
import com.hospital.service.AuditoriaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/** Consulta de la bitácora (solo lectura). */
@RestController
@RequestMapping("/api/auditoria")
@PreAuthorize("hasAuthority('AUDITORIA')")
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    public AuditoriaController(AuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    public record PaginaAuditoria(List<Auditoria> contenido, int pagina, int totalPaginas, long totalRegistros) { }

    @GetMapping
    public PaginaAuditoria buscar(@RequestParam(required = false) String usuario,
                                  @RequestParam(required = false) String entidad,
                                  @RequestParam(required = false) Operacion operacion,
                                  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
                                  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "20") int size) {
        PageRequest pr = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100),
                Sort.by(Sort.Direction.DESC, "fechaHora", "id"));
        Page<Auditoria> p = auditoriaService.buscar(usuario, entidad, operacion, desde, hasta, pr);
        return new PaginaAuditoria(p.getContent(), p.getNumber(), p.getTotalPages(), p.getTotalElements());
    }

    @GetMapping("/{entidad}/{id}")
    public List<Auditoria> historial(@PathVariable String entidad, @PathVariable Long id) {
        return auditoriaService.historialDe(entidad, id);
    }

    @GetMapping("/operaciones")
    public Operacion[] operaciones() {
        return Operacion.values();
    }
}
