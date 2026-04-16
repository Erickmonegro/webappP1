package com.webapp.webappstudents.controller;

import com.webapp.webappstudents.model.User;
import com.webapp.webappstudents.repository.UserRepository;
import com.webapp.webappstudents.service.DocumentoService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;

@Controller
public class DocumentoViewController {

    @Autowired
    private DocumentoService documentoService;

    // Inyectamos el repositorio para poder buscar al usuario
    @Autowired
    private UserRepository userRepository;

    //vista del pdf subido
    @GetMapping("/documentos")
    public String verDocumentos(Model model, Authentication authentication) {
        // 1. Enviamos la lista de documentos a la vista
        model.addAttribute("documentos", documentoService.obtenerTodos());

        // 2. Buscamos quién es el usuario logueado y enviamos su ID
        if (authentication != null && authentication.isAuthenticated()) {
            String emailLogueado = authentication.getName(); // Trae el email de Spring Security

            Optional<User> usuario = userRepository.findByEmail(emailLogueado);

            if (usuario.isPresent()) {
                // Si existe en la BD, mandamos su ID a Thymeleaf con el nombre "usuarioId"
                model.addAttribute("usuarioId", usuario.get().getId());
            }
        }

        return "documentos";
    }
}