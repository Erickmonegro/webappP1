package com.webapp.webappstudents.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "reservaciones")
@Data
public class Reservacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fechaEntrada;
    private LocalDate fechaSalida;

    @Column(name = "total_price")
    private Double precioTotal;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"password", "email", "creditos", "role", "reservations", "tareas"})
    private User user;

    @ManyToOne
    @JoinColumn(name = "accommodation_id", nullable = false)
    @JsonIgnoreProperties({"reservations", "description", "available"})
    private Alojamiento accommodation;
}