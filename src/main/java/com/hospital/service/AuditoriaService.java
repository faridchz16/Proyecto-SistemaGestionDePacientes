package com.hospital.service;

import com.hospital.model.Auditoria;
import com.hospital.model.Operacion;
import com.hospital.repository.AuditoriaRepository;
import com.hospital.repository.UsuarioRepository;
import com.hospital.security.SecurityUtils;
import jakarta.persistence.criteria.Predicate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;
    private final UsuarioRepository usuarioRepository;

    public AuditoriaService(AuditoriaRepository auditoriaRepository, UsuarioRepository usuarioRepository) {
        this.auditoriaRepository = auditoriaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /** Se une a la transacción del servicio de negocio que se está auditando. */
    @Transactional(propagation = Propagation.REQUIRED)
    public void registrar(Operacion operacion, String entidad, Long entidadId, String detalle) {
        registrarComo(SecurityUtils.usuarioActual(), operacion, entidad, entidadId, detalle);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void registrarComo(String usuario, Operacion operacion, String entidad, Long entidadId, String detalle) {
        String u = usuario == null ? SecurityUtils.SISTEMA : (usuario.length() > 50 ? usuario.substring(0, 50) : usuario);
        auditoriaRepository.save(new Auditoria(u, operacion, entidad, entidadId, detalle, ipActual()));
    }

    @Transactional
    public void actualizarUltimoAcceso(String username) {
        usuarioRepository.findByUsername(username).ifPresent(u -> u.setUltimoAcceso(LocalDateTime.now()));
    }

    @Transactional(readOnly = true)
    public Page<Auditoria> buscar(String usuario, String entidad, Operacion operacion,
                                  LocalDate desde, LocalDate hasta, Pageable pageable) {
        Specification<Auditoria> spec = (root, query, cb) -> {
            List<Predicate> p = new ArrayList<>();
            if (usuario != null && !usuario.isBlank())
                p.add(cb.like(cb.lower(root.get("usuario")), "%" + usuario.toLowerCase() + "%"));
            if (entidad != null && !entidad.isBlank()) p.add(cb.equal(root.get("entidad"), entidad));
            if (operacion != null) p.add(cb.equal(root.get("operacion"), operacion));
            if (desde != null) p.add(cb.greaterThanOrEqualTo(root.get("fechaHora"), desde.atStartOfDay()));
            if (hasta != null) p.add(cb.lessThan(root.get("fechaHora"), hasta.plusDays(1).atStartOfDay()));
            return cb.and(p.toArray(Predicate[]::new));
        };
        return auditoriaRepository.findAll(spec, pageable);
    }

    @Transactional(readOnly = true)
    public List<Auditoria> historialDe(String entidad, Long id) {
        return auditoriaRepository.findByEntidadAndEntidadIdOrderByFechaHoraDesc(entidad, id);
    }

    private String ipActual() {
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attrs) {
            HttpServletRequest req = attrs.getRequest();
            String fwd = req.getHeader("X-Forwarded-For");
            return fwd != null && !fwd.isBlank() ? fwd.split(",")[0].trim() : req.getRemoteAddr();
        }
        return null;
    }
}
