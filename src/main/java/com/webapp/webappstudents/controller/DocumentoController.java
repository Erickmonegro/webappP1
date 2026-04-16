package com.webapp.webappstudents.controller;

import com.webapp.webappstudents.dto.DocumentoDTO;
import com.webapp.webappstudents.model.Documento;
import com.webapp.webappstudents.service.DocumentoService;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Map;

@RestController
    @RequestMapping("/api/biblioteca")
    public class DocumentoController {

        private final DocumentoService documentoService;

    public DocumentoController(DocumentoService documentoService) {
        this.documentoService = documentoService;
    }

    //ver biblioteca(estudiante)
        @GetMapping("/user/{userId}")
        public List<DocumentoDTO> obtenerPorUsuario(@PathVariable Long userId) {
            return documentoService.obtenerPorUserId(userId)
                    .stream()
                    .map(this::toDTO)
                    .toList();
        }
       private DocumentoDTO toDTO(Documento doc) {
             return new DocumentoDTO(
                doc.getId(),
                doc.getTitulo(),
                doc.getFechaSubida(),
                doc.getUser() != null ? doc.getUser().getId() : null
             );
       }

        //para cuando se cliquee al documento subido, se pueda descargar
        @GetMapping("/download/{id}")
        public ResponseEntity<Resource> download(@PathVariable Long id) throws Exception {

            Documento doc = documentoService.obtenerPorId(id);

            File file = new File(doc.getRutaPdf());

            if (!file.exists()) {
                throw new RuntimeException("Archivo no existe");
            }

            String filename = file.getName();

            Resource resource = new UrlResource(file.toURI());


            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + filename + "\"")
                    .body(resource);
        }

        //subir archivo
        @PostMapping("/upload")
        public Documento subir(@RequestParam MultipartFile file,
                               @RequestParam Long userId) throws Exception {
            return documentoService.guardarArchivo(file, userId);
        }

        //Eliminar documento
        @DeleteMapping("/{id}")
        public String eliminar(@PathVariable Long id) {
            documentoService.eliminar(id);
            return "Documento eliminado";
        }

        //añade titulo nuevo al pdf
        @PutMapping("/{id}")
        public ResponseEntity<?> actualizar(@PathVariable Long id,
                                            @RequestBody Map<String, String> body) {

            Documento existente = documentoService.obtenerPorId(id);

            if (existente == null) {
                return ResponseEntity.status(404)
                        .body("Documento no encontrado ID: " + id);
            }

            System.out.println("EDIT ID: " + id);
            System.out.println("NUEVO TITULO: " + body.get("titulo"));

            existente.setTitulo(body.get("titulo"));

            documentoService.guardar(existente);

            return ResponseEntity.ok(existente);
        }



    }