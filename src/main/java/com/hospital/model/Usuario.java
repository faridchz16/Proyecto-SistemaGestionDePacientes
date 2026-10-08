package com.hospital.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
public class Usuario extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    /** Hash BCrypt; nunca se expone en la API. */
    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false, length = 80)
    private String nombres;

    @Column(nullable = false, length = 80)
    private String apellidos;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private boolean activo = true;

    @Column(name = "ultimo_acceso")
    private LocalDateTime ultimoAcceso;

    /** N usuarios -> 1 rol. FK: usuarios.rol_id -> roles.id */
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "rol_id", nullable = false, foreignKey = @ForeignKey(name = "fk_usuario_rol"))
    private Rol rol;

    @Override
    public Long getId() { return id; }

    @Override
    public String resumenAuditoria() {
        return "Usuario " + username + " (rol: " + (rol != null ? rol.getNombre() : null) + ", activo: " + activo + ")";
    }

    public String getNombreCompleto() { return nombres + " " + apellidos; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public LocalDateTime getUltimoAcceso() { return ultimoAcceso; }
    public void setUltimoAcceso(LocalDateTime ultimoAcceso) { this.ultimoAcceso = ultimoAcceso; }

    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }
}
