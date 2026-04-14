package com.webapp.webappstudents.security;

 // Ajusta tu paquete

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // 1. Rutas públicas (Tu login, registro, y tus archivos estáticos CSS/JS)
                        .requestMatchers("/login", "/registro", "/css/**", "/js/**", "/assets/**").permitAll()
                        // 2. Cualquier otra ruta requerirá estar logueado
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        // 3. Le decimos a Spring que use TU diseño de login, no el feo por defecto
                        .loginPage("/login")
                        // 4. A dónde lo enviamos si el login es exitoso
                        .defaultSuccessUrl("/dashboard", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout=true")
                        .permitAll()
                );

        return http.build();
    }

    // Bean para encriptar contraseñas (Lo usaremos cuando programemos el registro)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}