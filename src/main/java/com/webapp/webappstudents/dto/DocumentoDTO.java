package com.webapp.webappstudents.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class DocumentoDTO {

        private Long id;
        private String titulo;
        private LocalDateTime fechaSubida;
        private Long userId;

}