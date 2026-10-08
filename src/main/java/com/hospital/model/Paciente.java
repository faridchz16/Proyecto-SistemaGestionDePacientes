package com.hospital.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pacientes")
public class Paciente extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El tipo de documento es obligatorio")
    @Column(name = "tipo_documento", nullable = false)
    private String tipoDocumento;

    @NotBlank(message = "El número de documento es obligatorio")
    @Size(min = 8, max = 20, message = "El documento debe tener entre 8 y 20 caracteres")
    @Column(name = "numero_documento", nullable = false, unique = true)
    private String numeroDocumento;

    @NotBlank(message = "Los nombres son obligatorios")
    @Column(nullable = false)
    private String nombres;

    @NotBlank(message = "El apellido paterno es obligatorio")
    @Column(name = "apellido_paterno", nullable = false)
    private String apellidoPaterno;

    @NotBlank(message = "El apellido materno es obligatorio")
    @Column(name = "apellido_materno", nullable = false)
    private String apellidoMaterno;

    @Past(message = "La fecha de nacimiento debe ser anterior a hoy")
    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    private String sexo;
    private String telefono;

    @Email(message = "El correo no tiene un formato válido")
    private String correo;

    private String direccion;
    private String estado;

    /** 1 paciente -> N contactos de emergencia (FK contactos_emergencia.paciente_id). */
    @Valid
    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ContactoEmergencia> contactosEmergencia = new ArrayList<>();

    /** 1 paciente -> 1 historia clínica (lado inverso; la FK está en historias_clinicas.paciente_id). */
    @JsonIgnore
    @OneToOne(mappedBy = "paciente", fetch = FetchType.LAZY)
    private HistoriaClinica historiaClinica;

    /** 1 paciente -> N citas (lado inverso; la FK está en citas.paciente_id). */
    @JsonIgnore
    @OneToMany(mappedBy = "paciente", fetch = FetchType.LAZY)
    private List<Cita> citas = new ArrayList<>();

    @Override
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    @Override
    public String resumenAuditoria() {
        return "Paciente " + numeroDocumento + " - " + getNombreCompleto() + " (estado: " + estado + ")";
    }

    @JsonIgnore
    public String getNombreCompleto() {
        return String.join(" ", nombres == null ? "" : nombres,
                apellidoPaterno == null ? "" : apellidoPaterno,
                apellidoMaterno == null ? "" : apellidoMaterno).trim();
    }

    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }

    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidoPaterno() { return apellidoPaterno; }
    public void setApellidoPaterno(String apellidoPaterno) { this.apellidoPaterno = apellidoPaterno; }

    public String getApellidoMaterno() { return apellidoMaterno; }
    public void setApellidoMaterno(String apellidoMaterno) { this.apellidoMaterno = apellidoMaterno; }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public List<ContactoEmergencia> getContactosEmergencia() { return contactosEmergencia; }
    public void setContactosEmergencia(List<ContactoEmergencia> contactosEmergencia) { this.contactosEmergencia = contactosEmergencia; }

    public HistoriaClinica getHistoriaClinica() { return historiaClinica; }
    public List<Cita> getCitas() { return citas; }
}
