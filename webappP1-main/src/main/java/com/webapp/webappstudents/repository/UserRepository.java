package com.webapp.webappstudents.repository;

import com.webapp.webappstudents.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Spring Boot leerá este nombre y automáticamente creará la consulta:
    // "SELECT * FROM users WHERE email = ?"
    Optional<User> findByEmail(String email);

}