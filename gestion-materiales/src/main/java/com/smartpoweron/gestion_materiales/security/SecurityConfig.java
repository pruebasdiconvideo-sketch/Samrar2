package com.smartpoweron.gestion_materiales.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/api/auth/login", "/webjars/**", "/css/**", "/js/**", "/images/**").permitAll()

                // Rutas de Administración (V15, V16 y API Admin)
                .requestMatchers("/admin/**", "/api/admin/usuarios/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/admin/parametros-prueba").hasAnyRole("ADMIN", "INSPECTOR")
                .requestMatchers(HttpMethod.POST, "/api/admin/parametros-prueba/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/admin/parametros-prueba/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/admin/parametros-prueba/**").hasRole("ADMIN")

                // Rutas de Almacén e Inventario (V03, V04)
                .requestMatchers(HttpMethod.GET, "/almacen/catalogo", "/api/almacen/materiales", "/api/almacen/materiales/**").hasAnyRole("ADMIN", "ALMACENERO", "JEFE_PRODUCCION")
                .requestMatchers("/almacen/ingresos", "/api/almacen/ingresos").hasRole("ALMACENERO")
                .requestMatchers("/almacen/**", "/api/almacen/**").hasAnyRole("ADMIN", "ALMACENERO")

                // Rutas de Producción y Proyectos (V05, V06, V07)
                .requestMatchers("/produccion/aprobaciones", "/api/produccion/solicitudes/*/aprobar", "/api/produccion/solicitudes/*/rechazar").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/produccion/proyectos", "/produccion/solicitudes", "/api/produccion/**").hasAnyRole("JEFE_PRODUCCION", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/produccion/**").hasRole("JEFE_PRODUCCION")
                .requestMatchers(HttpMethod.PUT, "/api/produccion/**").hasAnyRole("JEFE_PRODUCCION", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/produccion/**").hasAnyRole("JEFE_PRODUCCION", "ADMIN")

                // Rutas de Despacho y Entrega (V08, V09, V10)
                .requestMatchers("/despacho/confirmar", "/api/despacho/pendientes-confirmacion", "/api/despacho/*/confirmar").hasRole("OPERARIO_ENSAMBLE")
                .requestMatchers("/despacho/**", "/api/despacho/**").hasRole("ALMACENERO")

                // Rutas de Calidad (V11)
                .requestMatchers(HttpMethod.GET, "/calidad/pruebas", "/api/calidad/pruebas").hasAnyRole("INSPECTOR", "JEFE_PRODUCCION", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/calidad/proyectos/*/certificado.pdf").hasAnyRole("INSPECTOR", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/calidad/pruebas").hasRole("INSPECTOR")
                .requestMatchers(HttpMethod.PUT, "/api/calidad/pruebas/**").hasRole("INSPECTOR")
                .requestMatchers(HttpMethod.DELETE, "/api/calidad/pruebas/**").hasAnyRole("INSPECTOR", "ADMIN")

                // Rutas de Mermas y Trazabilidad (V12, V13, V14)
                .requestMatchers("/merma/reporte", "/api/merma/reporte").hasRole("JEFE_PRODUCCION")
                .requestMatchers("/trazabilidad/**", "/api/trazabilidad/**").hasAnyRole("JEFE_PRODUCCION", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/merma/devoluciones").hasRole("OPERARIO_ENSAMBLE")
                .requestMatchers(HttpMethod.POST, "/api/merma/devoluciones/*/reintegrar", "/api/merma/devoluciones/*/marcar-merma").hasRole("ALMACENERO")
                .requestMatchers(HttpMethod.POST, "/api/merma/devoluciones/*/validar").hasRole("JEFE_PRODUCCION")
                .requestMatchers("/merma/**").hasAnyRole("OPERARIO_ENSAMBLE", "ALMACENERO", "JEFE_PRODUCCION")

                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .usernameParameter("correo")
                .passwordParameter("password")
                .defaultSuccessUrl("/inicio", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .deleteCookies("JWT_TOKEN", "JSESSIONID")
                .logoutSuccessUrl("/login?logout=true")
                .permitAll()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
