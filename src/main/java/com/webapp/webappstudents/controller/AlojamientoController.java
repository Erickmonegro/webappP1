package com.webapp.webappstudents.controller;

import com.webapp.webappstudents.model.Alojamiento;
import com.webapp.webappstudents.repository.AlojamientoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alojamiento")
public class AlojamientoController {
    private final AlojamientoRepository repository;

    public AlojamientoController(AlojamientoRepository repository){
        this.repository = repository;
    }

    @PostMapping
    public Alojamiento create_alojamiento(@RequestBody Alojamiento alojamiento) {
        return repository.save(alojamiento);
    }

    @GetMapping
    public List<Alojamiento> get_alojamientos(){
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Alojamiento> get_alojamiento_info (@PathVariable Long id){
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping ("/{id}")
    public ResponseEntity<Alojamiento> edit_alojamiento(@PathVariable Long id, @RequestBody Alojamiento details){
        return repository.findById(id).map(alojamiento->{
            alojamiento.setNombre(details.getNombre());
            alojamiento.setPrecioPorDia(details.getPrecioPorDia());
            alojamiento.setDescripcion(details.getDescripcion());
            alojamiento.setDisponible(details.getDisponible());
            alojamiento.setPiso(details.getPiso());
            return ResponseEntity.ok(repository.save(alojamiento));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete_alojamiento(@PathVariable Long id){
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/availables")
    public List<Alojamiento> get_available_alojamiento(){
        return repository.findAll().stream()
                .filter(Alojamiento::getDisponible)
                .toList();
    }
}


