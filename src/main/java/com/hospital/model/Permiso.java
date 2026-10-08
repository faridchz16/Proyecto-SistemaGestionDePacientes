package com.hospital.model;

import jakarta.persistence.*;

/**
 * Módulo del sistema al que un rol puede acceder (catálogo fijo, se carga al iniciar).
 * El "codigo" se usa como authority en Spring Security (p. ej. hasAuthority('PACIENTES')).
 */
@Entity
@Table(name = "permisos")
public class Permiso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String codigo;

    @Column(nullable = false, length = 60)
    private String nombre;

    /** Página del frontend asociada al módulo. */
    @Column(nullable = false, length = 60)
    private String ruta;

    @Column(length = 40)
    private String icono;

    /** Orden en el menú; el primer módulo permitido es la página de inicio tras el login. */
    @Column(nullable = false)
    private Integer orden;

    protected Permiso() { }

    public Permiso(String codigo, String nombre, String ruta, String icono, Integer orden) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.ruta = ruta;
        this.icono = icono;
        this.orden = orden;
    }

    public Long getId() { return id; }
    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public String getRuta() { return ruta; }
    public String getIcono() { return icono; }
    public Integer getOrden() { return orden; }
}
