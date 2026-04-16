package com.webapp.webappstudents.repository;

import com.webapp.webappstudents.model.Tarea;
import com.webapp.webappstudents.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TareaRepository extends JpaRepository<Tarea, Long> {
    // ¡Dejamos esto vacío!
    // JpaRepository ya tiene toda la magia por dentro para guardar y buscar Tareas.
    // Spring leerá esto como: "Busca todas las tareas cuyo dueño sea este usuario"
    List<Tarea> findByUser(User user);
}