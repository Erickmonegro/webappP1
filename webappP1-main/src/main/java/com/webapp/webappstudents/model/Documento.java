package com.webapp.webappstudents.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "documentos")
@Data
public class Documento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String rutaPdf;
    private LocalDateTime fechaSubida;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}