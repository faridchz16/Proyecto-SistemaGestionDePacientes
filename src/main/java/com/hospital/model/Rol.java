package com.hospital.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "roles")
public class Rol extends BaseEntity {

    public static final String ADMINISTRADOR = "ADMINISTRADOR";
    public static final String MEDICO = "MEDICO";
    public static final String RECEPCIONISTA = "RECEPCIONISTA";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 40)
    private String nombre;

    @Column(length = 200)
    private String descripcion;

    @Column(nullable = false)
    private boolean activo = true;

    /**
     * N roles <-> N permisos. Tabla intermedia rol_permiso con dos FKs.
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "rol_permiso",
            joinColumns = @JoinColumn(name = "rol_id", foreignKey = @ForeignKey(name = "fk_rolpermiso_rol")),
            inverseJoinColumns = @JoinColumn(name = "permiso_id", foreignKey = @ForeignKey(name = "fk_rolpermiso_permiso")))
    private Set<Permiso> permisos = new HashSet<>();

    /** 1 rol -> N usuarios (lado inverso; la FK está en usuarios.rol_id). */
    @OneToMany(mappedBy = "rol", fetch = FetchType.LAZY)
    private List<Usuario> usuarios = new ArrayList<>();

    protected Rol() { }

    public Rol(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    @Override
    public Long getId() { return id; }

    @Override
    public String resumenAuditoria() {
        return "Rol " + nombre + " (activo: " + activo + ", permisos: "
                + permisosOrdenados().stream().map(Permiso::getCodigo).toList() + ")";
    }

    public List<Permiso> permisosOrdenados() {
        return permisos.stream().sorted(Comparator.comparing(Permiso::getOrden)).toList();
    }

    public boolean esAdministrador() { return ADMINISTRADOR.equals(nombre); }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public Set<Permiso> getPermisos() { return permisos; }
    public void setPermisos(Set<Permiso> permisos) { this.permisos = permisos; }

    public List<Usuario> getUsuarios() { return usuarios; }
}
