package com.webapp.webappstudents.controller;

import com.webapp.webappstudents.model.User;
import com.webapp.webappstudents.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body("No autenticado");
        }

        return userRepository.findByEmail(principal.getName())
                .map(user -> ResponseEntity.ok(Map.of(
                        "id",       user.getId(),
                        "nombre",   user.getNombre(),
                        "email",    user.getEmail(),
                        "role",     user.getRole(),
                        "creditos", user.getCreditos()
                )))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }
}
