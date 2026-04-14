package com.webapp.webappstudents.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "tareas")
@Data
public class Tarea {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String descripcion;
    private String urgencia; // ALTA, MEDIA, BAJA
    private LocalDate fechaVencimiento;
    private Boolean completada = false;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true)
    private User user;
}