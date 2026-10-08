package com.hospital.audit;

import com.hospital.model.Operacion;
import com.hospital.service.AuditoriaService;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.authentication.event.LogoutSuccessEvent;
import org.springframework.stereotype.Component;

/** Registra en la bitácora los inicios de sesión exitosos, fallidos y cierres de sesión. */
@Component
public class LoginAuditListener {

    private final AuditoriaService auditoriaService;

    public LoginAuditListener(AuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    @EventListener
    public void exito(AuthenticationSuccessEvent event) {
        String usuario = event.getAuthentication().getName();
        auditoriaService.registrarComo(usuario, Operacion.LOGIN, "Sesion", null, "Inicio de sesión exitoso");
        auditoriaService.actualizarUltimoAcceso(usuario);
    }

    @EventListener
    public void fallo(AbstractAuthenticationFailureEvent event) {
        String usuario = String.valueOf(event.getAuthentication().getPrincipal());
        auditoriaService.registrarComo(usuario, Operacion.LOGIN_FALLIDO, "Sesion", null,
                "Intento fallido: " + event.getException().getMessage());
    }

    @EventListener
    public void logout(LogoutSuccessEvent event) {
        auditoriaService.registrarComo(event.getAuthentication().getName(), Operacion.LOGOUT, "Sesion", null,
                "Cierre de sesión");
    }
}
