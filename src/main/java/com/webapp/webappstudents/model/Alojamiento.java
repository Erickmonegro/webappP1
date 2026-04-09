package com.webapp.webappstudents.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Table(name = "alojamientos")
@Data
public class Alojamiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String descripcion;
    private Double precioPorDia;
    private Boolean disponible = true;

    // Relación bidireccional (Un alojamiento tiene muchas reservas)
    @OneToMany(mappedBy = "accommodation", cascade = CascadeType.ALL)
    private List<Reservacion> reservations;

    // Relación bidireccional con User para la lista de deseos
    @ManyToMany(mappedBy = "listaDeseos")
    private List<User> usuariosQueDesean;
}