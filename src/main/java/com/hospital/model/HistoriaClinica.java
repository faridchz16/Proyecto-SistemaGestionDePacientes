package com.hospital.model;

import jakarta.persistence.*;

import java.time.LocalDate;

/**
 * Historia clínica del paciente. Relación 1 a 1 con Paciente:
 * la FK historias_clinicas.paciente_id es UNIQUE, por lo que un paciente
 * no puede tener más de una historia.
 */
@Entity
@Table(name = "historias_clinicas")
public class HistoriaClinica extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_historia", nullable = false, unique = true, length = 20)
    private String numeroHistoria;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "paciente_id", nullable = false, unique = true,
            foreignKey = @ForeignKey(name = "fk_historia_paciente"))
    private Paciente paciente;

    @Column(name = "fecha_apertura", nullable = false)
    private LocalDate fechaApertura;

    @Column(name = "grupo_sanguineo", length = 5)
    private String grupoSanguineo;

    @Column(length = 500)
    private String alergias;

    @Column(name = "antecedentes_personales", length = 1000)
    private String antecedentesPersonales;

    @Column(name = "antecedentes_familiares", length = 1000)
    private String antecedentesFamiliares;

    @Column(length = 1000)
    private String observaciones;

    @Override
    public Long getId() { return id; }

    @Override
    public String resumenAuditoria() {
        return "Historia " + numeroHistoria + " del paciente ID " + (paciente != null ? paciente.getId() : null);
    }

    public String getNumeroHistoria() { return numeroHistoria; }
    public void setNumeroHistoria(String numeroHistoria) { this.numeroHistoria = numeroHistoria; }

    public Paciente getPaciente() { return paciente; }
    public void setPaciente(Paciente paciente) { this.paciente = paciente; }

    public LocalDate getFechaApertura() { return fechaApertura; }
    public void setFechaApertura(LocalDate fechaApertura) { this.fechaApertura = fechaApertura; }

    public String getGrupoSanguineo() { return grupoSanguineo; }
    public void setGrupoSanguineo(String grupoSanguineo) { this.grupoSanguineo = grupoSanguineo; }

    public String getAlergias() { return alergias; }
    public void setAlergias(String alergias) { this.alergias = alergias; }

    public String getAntecedentesPersonales() { return antecedentesPersonales; }
    public void setAntecedentesPersonales(String v) { this.antecedentesPersonales = v; }

    public String getAntecedentesFamiliares() { return antecedentesFamiliares; }
    public void setAntecedentesFamiliares(String v) { this.antecedentesFamiliares = v; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}
