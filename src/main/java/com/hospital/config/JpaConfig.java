package com.hospital.config;

import com.hospital.security.SecurityUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Optional;

/**
 * - JPA Auditing: llena creado_por / modificado_por con el usuario autenticado.
 * - La transacción se ordena con mayor precedencia que AuditoriaAspect para que la
 *   bitácora se grabe en la MISMA transacción que la operación auditada.
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@EnableTransactionManagement(proxyTargetClass = true, order = Ordered.HIGHEST_PRECEDENCE + 100)
public class JpaConfig {

    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> Optional.of(SecurityUtils.usuarioActual());
    }
}
