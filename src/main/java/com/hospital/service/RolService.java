package com.hospital.service;

import com.hospital.audit.Auditable;
import com.hospital.dto.RolRequest;
import com.hospital.exception.RecursoNoEncontradoException;
import com.hospital.exception.ReglaNegocioException;
import com.hospital.model.Operacion;
import com.hospital.model.Permiso;
import com.hospital.model.Rol;
import com.hospital.repository.PermisoRepository;
import com.hospital.repository.RolRepository;
import com.hospital.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class RolService {

    /** Permisos que el rol ADMINISTRADOR no puede perder (evita dejar el sistema sin administración). */
    private static final Set<String> PERMISOS_ADMIN_OBLIGATORIOS = Set.of("USUARIOS", "ROLES");

    private final RolRepository rolRepository;
    private final PermisoRepository permisoRepository;
    private final UsuarioRepository usuarioRepository;

    public RolService(RolRepository rolRepository, PermisoRepository permisoRepository, UsuarioRepository usuarioRepository) {
        this.rolRepository = rolRepository;
        this.permisoRepository = permisoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<Rol> listar() {
        return rolRepository.findAllByOrderByIdAsc();
    }

    @Transactional(readOnly = true)
    public Rol obtener(Long id) {
        return rolRepository.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("el rol", id));
    }

    @Transactional(readOnly = true)
    public long contarUsuarios(Long rolId) {
        return usuarioRepository.countByRolId(rolId);
    }

    @Transactional(readOnly = true)
    public List<Permiso> listarPermisos() {
        return permisoRepository.findAllByOrderByOrdenAsc();
    }

    @Transactional
    @Auditable(entidad = "Rol", operacion = Operacion.REGISTRAR)
    public Rol registrar(RolRequest req) {
        String nombre = normalizar(req.nombre());
        if (rolRepository.existsByNombre(nombre)) {
            throw new ReglaNegocioException("Ya existe un rol con el nombre " + nombre);
        }
        Rol rol = new Rol(nombre, req.descripcion());
        rol.setPermisos(cargarPermisos(req.permisoIds()));
        return rolRepository.save(rol);
    }

    @Transactional
    @Auditable(entidad = "Rol", operacion = Operacion.MODIFICAR)
    public Rol actualizar(Long id, RolRequest req) {
        Rol rol = obtener(id);
        String nombre = normalizar(req.nombre());
        if (rolRepository.existsByNombreAndIdNot(nombre, id)) {
            throw new ReglaNegocioException("Ya existe un rol con el nombre " + nombre);
        }
        Set<Permiso> permisos = cargarPermisos(req.permisoIds());
        if (rol.esAdministrador()) {
            if (!Rol.ADMINISTRADOR.equals(nombre)) {
                throw new ReglaNegocioException("El rol ADMINISTRADOR no puede renombrarse");
            }
            Set<String> codigos = new HashSet<>();
            permisos.forEach(p -> codigos.add(p.getCodigo()));
            if (!codigos.containsAll(PERMISOS_ADMIN_OBLIGATORIOS)) {
                throw new ReglaNegocioException("El rol ADMINISTRADOR debe conservar los módulos Usuarios y Roles");
            }
        }
        rol.setNombre(nombre);
        rol.setDescripcion(req.descripcion());
        rol.setPermisos(permisos);
        return rol;
    }

    @Transactional
    @Auditable(entidad = "Rol", cambioEstado = true)
    public Rol cambiarEstado(Long id, boolean activo) {
        Rol rol = obtener(id);
        if (!activo && rol.esAdministrador()) {
            throw new ReglaNegocioException("El rol ADMINISTRADOR no puede desactivarse");
        }
        rol.setActivo(activo);
        return rol;
    }

    private Set<Permiso> cargarPermisos(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new ReglaNegocioException("Debe asignar al menos un módulo al rol");
        }
        List<Permiso> permisos = permisoRepository.findAllById(ids);
        if (permisos.size() != ids.size()) {
            throw new ReglaNegocioException("Uno o más módulos seleccionados no existen");
        }
        return new HashSet<>(permisos);
    }

    /** "Médico general" -> "MEDICO_GENERAL" */
    static String normalizar(String nombre) {
        String sinTildes = Normalizer.normalize(nombre.trim(), Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return sinTildes.toUpperCase().replaceAll("\\s+", "_");
    }
}
