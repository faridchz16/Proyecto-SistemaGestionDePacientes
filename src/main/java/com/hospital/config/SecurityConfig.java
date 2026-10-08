package com.hospital.config;

import com.hospital.security.CsrfCookieFilter;
import com.hospital.security.RedireccionPorRolHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * Control de acceso (Pregunta 5).
 * Capa 1 - URL: cada página y cada endpoint exige el authority del módulo.
 * Capa 2 - Método: los controladores llevan @PreAuthorize (EnableMethodSecurity).
 * El frontend además oculta las opciones del menú no permitidas, pero la seguridad real está aquí.
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private static final RequestMatcher API = PathPatternRequestMatcher.withDefaults().matcher("/api/**");

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, RedireccionPorRolHandler successHandler) throws Exception {
        CsrfTokenRequestAttributeHandler csrfHandler = new CsrfTokenRequestAttributeHandler();
        csrfHandler.setCsrfRequestAttributeName(null);

        http
            .authorizeHttpRequests(auth -> auth
                // Público
                    .requestMatchers("/login.html", "/403.html", "/css/*", "/js/", "/img/*", "/favicon.ico", "/error").permitAll()
                // Administración
                .requestMatchers("/usuarios.html", "/api/usuarios/**").hasAuthority("USUARIOS")
                .requestMatchers(HttpMethod.GET, "/api/roles").hasAnyAuthority("ROLES", "USUARIOS")
                .requestMatchers("/roles.html", "/api/roles/**", "/api/permisos/**").hasAuthority("ROLES")
                .requestMatchers("/auditoria.html", "/api/auditoria/**").hasAuthority("AUDITORIA")
                // Clínico
                .requestMatchers("/api/pacientes/*/atenciones/**", "/api/pacientes/*/atenciones").hasAuthority("HISTORIAS")
                .requestMatchers("/historias.html", "/api/historias/**").hasAuthority("HISTORIAS")
                .requestMatchers("/citas.html", "/api/citas/**").hasAuthority("CITAS")
                // La búsqueda de pacientes la usan Pacientes, Historias y Citas
                .requestMatchers(HttpMethod.GET, "/api/pacientes", "/api/pacientes/*")
                    .hasAnyAuthority("PACIENTES", "HISTORIAS", "CITAS")
                .requestMatchers("/pacientes.html", "/api/pacientes/**").hasAuthority("PACIENTES")
                // Cualquier otro recurso (inicio, /api/auth/me) solo con sesión iniciada
                .anyRequest().authenticated())
            .formLogin(form -> form
                .loginPage("/login.html")
                .loginProcessingUrl("/login")
                .successHandler(successHandler)
                .failureHandler(failureHandler())
                .permitAll())
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login.html?logout")
                .deleteCookies("JSESSIONID"))
            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .csrfTokenRequestHandler(csrfHandler))
            .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)
            .exceptionHandling(ex -> ex
                // API: 401/403 en JSON; páginas: redirección al login o a 403.html
                .defaultAuthenticationEntryPointFor(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED), API)
                .defaultAuthenticationEntryPointFor(new LoginUrlAuthenticationEntryPoint("/login.html"), request -> true)
                .accessDeniedHandler((request, response, denied) -> {
                    if (API.matches(request)) {
                        response.setStatus(HttpStatus.FORBIDDEN.value());
                        response.setContentType("application/json;charset=UTF-8");
                        response.getWriter().write(
                            "{\"status\":403,\"error\":\"Forbidden\",\"mensaje\":\"No tiene permisos para acceder a este recurso\"}");
                    } else {
                        response.sendRedirect(request.getContextPath() + "/403.html");
                    }
                }));

        return http.build();
    }

    private AuthenticationFailureHandler failureHandler() {
        return (request, response, exception) -> {
            String motivo = exception instanceof DisabledException ? "inactivo" : "credenciales";
            response.sendRedirect(request.getContextPath() + "/login.html?error=" + motivo);
        };
    }
}
