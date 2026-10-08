package com.hospital.exception;

public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String entidad, Object id) {
        super("No se encontró " + entidad + " con ID: " + id);
    }
}
