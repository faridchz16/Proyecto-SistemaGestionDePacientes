package com.hospital.config;

import com.hospital.model.*;
import com.hospital.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Carga inicial idempotente (solo inserta lo que falta):
 * módulos (permisos), roles base, usuarios de prueba y, si la tabla está vacía, pacientes de ejemplo.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final PermisoRepository permisoRepository;
    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final PacienteRepository pacienteRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(PermisoRepository permisoRepository, RolRepository rolRepository,
                           UsuarioRepository usuarioRepository, PacienteRepository pacienteRepository,
                           PasswordEncoder passwordEncoder) {
        this.permisoRepository = permisoRepository;
        this.rolRepository = rolRepository;
        this.usuarioRepository = usuarioRepository;
        this.pacienteRepository = pacienteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        List<Permiso> catalogo = List.of(
                new Permiso("USUARIOS", "Usuarios", "/usuarios.html", "bi-people-fill", 1),
                new Permiso("ROLES", "Roles", "/roles.html", "bi-shield-lock-fill", 2),
                new Permiso("AUDITORIA", "Auditoría", "/auditoria.html", "bi-journal-text", 3),
                new Permiso("HISTORIAS", "Historias clínicas", "/historias.html", "bi-journal-medical", 4),
                new Permiso("CITAS", "Citas", "/citas.html", "bi-calendar-check", 5),
                new Permiso("PACIENTES", "Pacientes", "/pacientes.html", "bi-person-vcard", 6));
        for (Permiso p : catalogo) {
            if (permisoRepository.findByCodigo(p.getCodigo()).isEmpty()) {
                permisoRepository.save(p);
            }
        }
        Map<String, Permiso> permisos = permisoRepository.findAll().stream()
                .collect(Collectors.toMap(Permiso::getCodigo, p -> p));

        Rol admin = crearRol(Rol.ADMINISTRADOR, "Acceso total: usuarios, roles, auditoría y módulos del sistema",
                permisos, "USUARIOS", "ROLES", "AUDITORIA", "HISTORIAS", "CITAS", "PACIENTES");
        Rol medico = crearRol(Rol.MEDICO, "Gestión de pacientes e historias clínicas",
                permisos, "HISTORIAS", "PACIENTES");
        Rol recepcion = crearRol(Rol.RECEPCIONISTA, "Registro de pacientes y programación de citas",
                permisos, "CITAS", "PACIENTES");

        crearUsuario("admin", "Admin2026", "Ana", "Torres Ríos", "admin@hospital.pe", admin);
        crearUsuario("medico", "Medico2026", "Carlos", "Mendoza Paz", "cmendoza@hospital.pe", medico);
        crearUsuario("medico2", "Medico2026", "Lucía", "Fernández Soto", "lfernandez@hospital.pe", medico);
        crearUsuario("recepcion", "Recepcion2026", "María", "Quispe Huamán", "mquispe@hospital.pe", recepcion);

        if (pacienteRepository.count() == 0) {
            crearPaciente("45879632", "Jorge", "Ramírez", "Salas", LocalDate.of(1985, 4, 12), "Masculino");
            crearPaciente("70125489", "Rosa", "Gutiérrez", "Lima", LocalDate.of(1992, 9, 3), "Femenino");
            crearPaciente("10457896", "Pedro", "Castillo", "Vega", LocalDate.of(1970, 1, 25), "Masculino");
        }
        log.info("Datos iniciales verificados: {} módulos, {} roles, {} usuarios",
                permisoRepository.count(), rolRepository.count(), usuarioRepository.count());
    }

    private Rol crearRol(String nombre, String descripcion, Map<String, Permiso> permisos, String... codigos) {
        return rolRepository.findByNombre(nombre).orElseGet(() -> {
            Rol rol = new Rol(nombre, descripcion);
            Set<Permiso> set = new HashSet<>();
            for (String c : codigos) set.add(permisos.get(c));
            rol.setPermisos(set);
            return rolRepository.save(rol);
        });
    }

    private void crearUsuario(String username, String password, String nombres, String apellidos,
                              String email, Rol rol) {
        if (usuarioRepository.existsByUsername(username)) return;
        Usuario u = new Usuario();
        u.setUsername(username);
        u.setPassword(passwordEncoder.encode(password));
        u.setNombres(nombres);
        u.setApellidos(apellidos);
        u.setEmail(email);
        u.setRol(rol);
        usuarioRepository.save(u);
    }

    private void crearPaciente(String dni, String nombres, String apP, String apM, LocalDate nac, String sexo) {
        Paciente p = new Paciente();
        p.setTipoDocumento("DNI");
        p.setNumeroDocumento(dni);
        p.setNombres(nombres);
        p.setApellidoPaterno(apP);
        p.setApellidoMaterno(apM);
        p.setFechaNacimiento(nac);
        p.setSexo(sexo);
        p.setEstado("ACTIVO");
        pacienteRepository.save(p);
    }
}
