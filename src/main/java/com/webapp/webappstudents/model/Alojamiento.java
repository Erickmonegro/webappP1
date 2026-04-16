package com.webapp.webappstudents.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
@Table(name = "alojamientos")
public class Alojamiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private Integer piso;
    private Double precioPorDia;
    private String descripcion;
    private Boolean disponible = true;

    // Relación bidireccional (Un alojamiento tiene muchas reservas)
    @OneToMany(mappedBy = "accommodation", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Reservacion> reservations;

    // Relación bidireccional con User para la lista de deseos
    @ManyToMany(mappedBy = "listaDeseos")
    @JsonIgnore
    private List<User> usuariosQueDesean;
}