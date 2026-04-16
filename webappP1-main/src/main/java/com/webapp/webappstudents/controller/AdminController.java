package com.webapp.webappstudents.controller;

import com.webapp.webappstudents.model.User;
import com.webapp.webappstudents.model.Rol;
import com.webapp.webappstudents.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/usuarios")
    public String listarUsuarios(Model model) {
        List<User> usuarios = userRepository.findAll();
        model.addAttribute("usuarios", usuarios);
        return "admin/usuarios"; // Buscara src/main/resources/templates/admin/usuarios.html
    }

    @PostMapping("/usuarios/cambiar-rol/{id}")
    public String cambiarRol(@PathVariable Long id, @RequestParam Rol nuevoRol) {
        User usuario = userRepository.findById(id).orElseThrow();
        usuario.setRole(nuevoRol);
        userRepository.save(usuario);
        return "redirect:/admin/usuarios";
    }
}