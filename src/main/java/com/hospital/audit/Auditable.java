package com.hospital.audit;

import com.hospital.model.Operacion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marca un método de servicio cuya ejecución exitosa debe quedar registrada
 * en la bitácora de auditoría. Lo intercepta {@link AuditoriaAspect}.
 *
 * <pre>
 * &#64;Auditable(entidad = "Paciente", operacion = Operacion.REGISTRAR)
 * public Paciente registrar(Paciente p) { ... }
 * </pre>
 *
 * Si {@code cambioEstado = true}, la operación se decide en tiempo de ejecución
 * según el argumento booleano del método: true = ACTIVAR, false = DESACTIVAR.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Auditable {

    String entidad();

    Operacion operacion() default Operacion.MODIFICAR;

    boolean cambioEstado() default false;
}
