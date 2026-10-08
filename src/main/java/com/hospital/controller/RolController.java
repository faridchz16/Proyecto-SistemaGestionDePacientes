package com.hospital.controller;

import com.hospital.dto.PermisoResponse;
import com.hospital.dto.RolRequest;
import com.hospital.dto.RolResponse;
import com.hospital.model.Rol;
import com.hospital.service.RolService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RolController {

    private final RolService rolService;

    public RolController(RolService rolService) {
        this.rolService = rolService;
    }

    /** También lo usa el formulario de usuarios para el combo de roles. */
    @GetMapping("/roles")
    @PreAuthorize("hasAnyAuthority('ROLES','USUARIOS')")
    public List<RolResponse> listar() {
        return rolService.listar().stream().map(this::aDto).toList();
    }

    @GetMapping("/roles/{id}")
    @PreAuthorize("hasAuthority('ROLES')")
    public RolResponse obtener(@PathVariable Long id) {
        return aDto(rolService.obtener(id));
    }

    @PostMapping("/roles")
    @PreAuthorize("hasAuthority('ROLES')")
    public ResponseEntity<RolResponse> registrar(@Valid @RequestBody RolRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(aDto(rolService.registrar(req)));
    }

    @PutMapping("/roles/{id}")
    @PreAuthorize("hasAuthority('ROLES')")
    public RolResponse actualizar(@PathVariable Long id, @Valid @RequestBody RolRequest req) {
        return aDto(rolService.actualizar(id, req));
    }

    @PatchMapping("/roles/{id}/estado")
    @PreAuthorize("hasAuthority('ROLES')")
    public RolResponse cambiarEstado(@PathVariable Long id, @RequestParam boolean activo) {
        return aDto(rolService.cambiarEstado(id, activo));
    }

    /** Catálogo de módulos que se pueden asignar a un rol. */
    @GetMapping("/permisos")
    @PreAuthorize("hasAuthority('ROLES')")
    public List<PermisoResponse> permisos() {
        return rolService.listarPermisos().stream().map(PermisoResponse::de).toList();
    }

    private RolResponse aDto(Rol rol) {
        return RolResponse.de(rol, rolService.contarUsuarios(rol.getId()));
    }
}
