package com.hospital.model;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Superclase de auditoría a nivel de registro (Spring Data JPA Auditing).
 * Cada entidad que la extiende guarda automáticamente quién y cuándo la creó
 * y la modificó por última vez. Complementa la bitácora de la tabla "auditoria".
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @CreatedBy
    @Column(name = "creado_por", length = 50, updatable = false)
    private String creadoPor;

    @CreatedDate
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @LastModifiedBy
    @Column(name = "modificado_por", length = 50)
    private String modificadoPor;

    @LastModifiedDate
    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;

    public abstract Long getId();

    /** Texto breve que se guarda en el campo "detalle" de la bitácora de auditoría. */
    public String resumenAuditoria() {
        return "";
    }

    public String getCreadoPor() { return creadoPor; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public String getModificadoPor() { return modificadoPor; }
    public LocalDateTime getFechaModificacion() { return fechaModificacion; }
}
