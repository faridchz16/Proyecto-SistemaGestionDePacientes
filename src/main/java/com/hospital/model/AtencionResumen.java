package com.hospital.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "atenciones_resumen")
public class AtencionResumen extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "La fecha de atención es obligatoria")
    @Column(name = "fecha_atencion")
    private String fechaAtencion;

    @NotBlank(message = "La especialidad es obligatoria")
    private String especialidad;

    private String medico;

    @Column(length = 1000)
    private String diagnostico;

    /** N atenciones -> 1 paciente. FK: atenciones_resumen.paciente_id -> pacientes.id */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "paciente_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_atencion_paciente"))
    @JsonIgnore
    private Paciente paciente;

    @Override
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    @Override
    public String resumenAuditoria() {
        return "Atención " + fechaAtencion + " - " + especialidad + " (" + medico + ")";
    }

    public String getFechaAtencion() { return fechaAtencion; }
    public void setFechaAtencion(String fechaAtencion) { this.fechaAtencion = fechaAtencion; }

    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }

    public String getMedico() { return medico; }
    public void setMedico(String medico) { this.medico = medico; }

    public String getDiagnostico() { return diagnostico; }
    public void setDiagnostico(String diagnostico) { this.diagnostico = diagnostico; }

    public Paciente getPaciente() { return paciente; }
    public void setPaciente(Paciente paciente) { this.paciente = paciente; }
}
