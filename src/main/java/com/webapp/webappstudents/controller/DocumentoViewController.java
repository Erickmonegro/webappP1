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

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/documentos")
    public String verDocumentos(Model model, Authentication authentication) {
        // Enviar la lista de documentos a la vista
        model.addAttribute("documentos", documentoService.obtenerTodos());

        // Buscar el usuario logueado y enviar su ID a Thymeleaf
        if (authentication != null && authentication.isAuthenticated()) {
            String emailLogueado = authentication.getName();

            Optional<User> usuario = userRepository.findByEmail(emailLogueado);

            if (usuario.isPresent()) {
                model.addAttribute("usuarioId", usuario.get().getId());
            }
        }

        return "documentos";
    }
}
