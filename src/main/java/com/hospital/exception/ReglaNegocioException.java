package com.hospital.exception;

/** Violación de una regla de negocio (duplicados, estados no permitidos, etc.). Responde 409. */
public class ReglaNegocioException extends RuntimeException {
    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
