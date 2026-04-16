package com.webapp.webappstudents.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Table(name = "users") // <-- CAMBIO CRÍTICO: "users" en plural, porque "user" explota en Postgres
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private String nombre;

    private Double creditos = 10000.0;

    @Enumerated(EnumType.STRING)
    private Rol role;

    // Relaciones (Un usuario tiene muchas reservas, documentos y tareas)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Reservacion> reservations; // Requiere crear Reservacion.java

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Documento> documentos; // <-- Corregido a singular: Documento (Requiere crear Documento.java)

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Tarea> tareas; // <-- Corregido a singular: Tarea (Requiere crear Tarea.java)

    // Lista de deseados (Muchos usuarios pueden desear muchos alojamientos)
    @ManyToMany
    @JoinTable(
            name = "lista_deseos", // Buena práctica: usar snake_case para tablas en SQL
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "alojamiento_id") // Ajustado a alojamiento
    )
    @JsonIgnore
    private List<Alojamiento> listaDeseos; // <-- Corregido a singular: Alojamiento (Requiere crear Alojamiento.java)
}