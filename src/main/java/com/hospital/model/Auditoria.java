package com.hospital.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Bitácora de auditoría. Es de solo inserción: el sistema no expone
 * operaciones para modificar ni borrar sus registros.
 */
@Entity
@Table(name = "auditoria", indexes = {
        @Index(name = "idx_auditoria_entidad", columnList = "entidad, entidad_id"),
        @Index(name = "idx_auditoria_usuario", columnList = "usuario"),
        @Index(name = "idx_auditoria_fecha", columnList = "fecha_hora")})
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String usuario;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Operacion operacion;

    @Column(nullable = false, length = 50)
    private String entidad;

    @Column(name = "entidad_id")
    private Long entidadId;

    @Column(length = 1000)
    private String detalle;

    @Column(length = 45)
    private String ip;

    protected Auditoria() { }

    public Auditoria(String usuario, Operacion operacion, String entidad, Long entidadId, String detalle, String ip) {
        this.usuario = usuario;
        this.fechaHora = LocalDateTime.now();
        this.operacion = operacion;
        this.entidad = entidad;
        this.entidadId = entidadId;
        this.detalle = detalle != null && detalle.length() > 1000 ? detalle.substring(0, 1000) : detalle;
        this.ip = ip;
    }

    public Long getId() { return id; }
    public String getUsuario() { return usuario; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public Operacion getOperacion() { return operacion; }
    public String getEntidad() { return entidad; }
    public Long getEntidadId() { return entidadId; }
    public String getDetalle() { return detalle; }
    public String getIp() { return ip; }
}
