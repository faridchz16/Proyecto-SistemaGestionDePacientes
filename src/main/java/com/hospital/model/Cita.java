package com.hospital.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Cita médica. Tabla intermedia "con datos" entre Paciente y Usuario (médico):
 *  - N citas -> 1 paciente  (FK citas.paciente_id -> pacientes.id)
 *  - N citas -> 1 médico    (FK citas.medico_id   -> usuarios.id)
 * El cruce de horario de un médico se valida en CitaService (las citas canceladas liberan el horario).
 */
@Entity
@Table(name = "citas", indexes = @Index(name = "idx_cita_medico_fecha", columnList = "medico_id, fecha_hora"))
public class Cita extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "paciente_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_cita_paciente"))
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "medico_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_cita_medico"))
    private Usuario medico;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Column(nullable = false, length = 80)
    private String especialidad;

    @Column(length = 500)
    private String motivo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private EstadoCita estado = EstadoCita.PROGRAMADA;

    @Override
    public Long getId() { return id; }

    @Override
    public String resumenAuditoria() {
        return "Cita " + fechaHora + " paciente ID " + (paciente != null ? paciente.getId() : null)
                + " médico " + (medico != null ? medico.getUsername() : null) + " [" + estado + "]";
    }

    public Paciente getPaciente() { return paciente; }
    public void setPaciente(Paciente paciente) { this.paciente = paciente; }

    public Usuario getMedico() { return medico; }
    public void setMedico(Usuario medico) { this.medico = medico; }

    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }

    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public EstadoCita getEstado() { return estado; }
    public void setEstado(EstadoCita estado) { this.estado = estado; }
}
