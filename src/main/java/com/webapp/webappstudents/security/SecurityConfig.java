package com.webapp.webappstudents.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomAuthSuccessHandler successHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth

                // ── Recursos estáticos y páginas de auth: públicos ──────────
                .requestMatchers("/login", "/registro", "/css/**", "/js/**", "/assets/**").permitAll()

                // ── API: alojamientos disponibles (lectura pública) ──────────
                .requestMatchers(HttpMethod.GET, "/api/alojamiento", "/api/alojamiento/**").permitAll()

                // ── API: CRUD de alojamientos y lista de usuarios → solo ADMIN
                .requestMatchers(HttpMethod.POST,   "/api/alojamiento").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT,    "/api/alojamiento/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/alojamiento/**").hasRole("ADMIN")
                .requestMatchers("/api/users").hasRole("ADMIN")

                // ── API: datos del usuario autenticado ───────────────────────
                .requestMatchers("/api/me", "/api/reservacion/me").authenticated()

                // ── Resto de /api/** público ─────────────────────────────────
                .requestMatchers("/api/**").permitAll()

                // ── Páginas protegidas por rol ───────────────────────────────
                .requestMatchers("/admin").hasRole("ADMIN")
                .requestMatchers("/dashboard", "/documentos", "/alojamientos").authenticated()

                // ── Cualquier otra ruta requiere login ───────────────────────
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .successHandler(successHandler)   // ← handler de redirección por rol
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout=true")
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}