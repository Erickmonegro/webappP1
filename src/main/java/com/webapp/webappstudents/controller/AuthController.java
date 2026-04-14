package com.webapp.webappstudents.controller;

import com.webapp.webappstudents.dto.RegistroDTO;
import com.webapp.webappstudents.model.Rol;
import com.webapp.webappstudents.model.User;
import com.webapp.webappstudents.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    @Autowired
    private UserRepository usuarioRepositorio;

    @Autowired
    private PasswordEncoder passwordEncoder; // Herramienta para encriptar

    // ==========================================
    // Mostrar la página de Login
    // ==========================================
    @GetMapping("/login")
    public String mostrarLogin() {
        return "login"; // Esto busca tu archivo login.html
    }

    // ==========================================
    // 1. Mostrar la página de registro
    // ==========================================
    @GetMapping("/registro")
    public String mostrarFormularioDeRegistro(Model model) {
        // Le pasamos un DTO vacío al HTML para que el usuario lo llene
        model.addAttribute("usuario", new RegistroDTO());
        return "registro"; // Esto busca un archivo registro.html
    }

    // ==========================================
    // 2. Procesar el formulario y guardar en BD
    // ==========================================
    @PostMapping("/registro")
    public String registrarCuentaDeUsuario(@ModelAttribute("usuario") RegistroDTO registroDTO) {

        // 1. Convertimos el DTO en un Usuario real
        User nuevoUsuario = new User();
        nuevoUsuario.setNombre(registroDTO.getNombre());
        nuevoUsuario.setEmail(registroDTO.getEmail());

        // 2. ENCRIPTAMOS LA CONTRASEÑA (Regla de oro de ciberseguridad)
        nuevoUsuario.setPassword(passwordEncoder.encode(registroDTO.getPassword()));


        // 3. Le asignamos el rol por defecto
        nuevoUsuario.setRole(Rol.ROLE_ESTUDIANTE);

        nuevoUsuario.setCreditos(0.0);
        // 4. Guardamos en PostgreSQL
        usuarioRepositorio.save(nuevoUsuario);

        // 5. Lo mandamos al login para que inicie sesión, pasando un mensaje de éxito
        return "redirect:/login?exito";
    }
}