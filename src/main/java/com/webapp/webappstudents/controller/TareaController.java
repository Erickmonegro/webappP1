package com.webapp.webappstudents.controller;

import com.webapp.webappstudents.model.DiaCalendario;
import com.webapp.webappstudents.model.Tarea;
import com.webapp.webappstudents.model.User;
import com.webapp.webappstudents.repository.TareaRepository;
import com.webapp.webappstudents.repository.UserRepository;
import com.webapp.webappstudents.service.CalendarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/tareas") // Todas las rutas aquí empezarán con /tareas
public class TareaController {

    @Autowired
    private TareaRepository tareaRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CalendarService calendarService;

    // ==========================================
    // 1. MOSTRAR EL TABLERO Y EL CALENDARIO
    // ==========================================
    @GetMapping
    public String showTareas(Model model, Principal principal) {
        // 1. Identificamos quién está usando la app
        String emailLogueado = principal.getName();
        User usuarioActual = userRepository.findByEmail(emailLogueado).orElseThrow();

        // 2. Traemos SOLO las tareas de esa persona
        List<Tarea> misTareas = tareaRepository.findByUser(usuarioActual);

        // 3. Generamos el calendario usando las tareas de esa persona
        List<DiaCalendario> diasCalendario = calendarService.generarCalendarioActual(misTareas);

        // 4. Empacamos y enviamos al HTML
        model.addAttribute("tareas", misTareas);
        model.addAttribute("diasCal", diasCalendario);

        return "tareas";
    }

    // ==========================================
    // 2. GUARDAR UNA TAREA NUEVA
    // ==========================================
    @PostMapping("/guardar")
    public String guardarNuevaTarea(Tarea nuevaTarea, Principal principal) {
        // Identificamos al dueño
        String emailLogueado = principal.getName();
        User usuarioActual = userRepository.findByEmail(emailLogueado).orElseThrow();

        // Le asignamos el dueño a la tarea antes de guardar
        nuevaTarea.setUser(usuarioActual);

        tareaRepository.save(nuevaTarea);
        return "redirect:/tareas";
    }

    // ==========================================
    // 3. MARCAR COMO COMPLETADA
    // ==========================================
    @PostMapping("/completar/{id}")
    public String completarTarea(@PathVariable Long id) {
        Tarea tarea = tareaRepository.findById(id).orElse(null);

        if (tarea != null) {
            tarea.setCompletada(!tarea.getCompletada());
            tareaRepository.save(tarea);
        }
        return "redirect:/tareas";
    }

    // ==========================================
    // 4. ELIMINAR TAREA
    // ==========================================
    @PostMapping("/eliminar/{id}")
    public String eliminarTarea(@PathVariable Long id) {
        tareaRepository.deleteById(id);
        return "redirect:/tareas";
    }

}