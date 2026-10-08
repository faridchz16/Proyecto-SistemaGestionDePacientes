package com.hospital.audit;

import com.hospital.model.BaseEntity;
import com.hospital.model.Operacion;
import com.hospital.service.AuditoriaService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

/**
 * Aspecto (AOP) que registra en la tabla "auditoria" cada operación exitosa
 * de los métodos anotados con {@link Auditable}.
 *
 * Se ejecuta DENTRO de la transacción del servicio (orden de menor precedencia que
 * el interceptor transaccional, ver JpaConfig), así que si la operación de negocio
 * hace rollback, el registro de auditoría también se revierte.
 */
@Aspect
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class AuditoriaAspect {

    private final AuditoriaService auditoriaService;

    public AuditoriaAspect(AuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    @AfterReturning(pointcut = "@annotation(auditable)", returning = "resultado", argNames = "auditable,resultado")
    public void auditar(JoinPoint jp, Auditable auditable, Object resultado) {
        Operacion operacion = auditable.operacion();
        if (auditable.cambioEstado()) {
            operacion = buscarBooleano(jp.getArgs()) ? Operacion.ACTIVAR : Operacion.DESACTIVAR;
        }

        Long entidadId = null;
        String detalle = null;
        if (resultado instanceof BaseEntity entidad) {
            entidadId = entidad.getId();
            detalle = entidad.resumenAuditoria();
        } else if (jp.getArgs().length > 0 && jp.getArgs()[0] instanceof Long id) {
            entidadId = id;
        }

        auditoriaService.registrar(operacion, auditable.entidad(), entidadId, detalle);
    }

    private boolean buscarBooleano(Object[] args) {
        for (Object arg : args) {
            if (arg instanceof Boolean b) return b;
        }
        return true;
    }
}
