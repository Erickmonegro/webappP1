package com.webapp.webappstudents.controller;

import com.webapp.webappstudents.repository.AlojamientoRepository;
import com.webapp.webappstudents.repository.ReservacionRepository;
import com.webapp.webappstudents.repository.UserRepository;
import com.webapp.webappstudents.model.Reservacion;
import com.webapp.webappstudents.model.User;
import com.webapp.webappstudents.model.Alojamiento;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/reservacion")
public class ReservacionController {

    private final ReservacionRepository reservacionRepository;
    private final AlojamientoRepository alojamientoRepository;
    private final UserRepository userRepository;

    public ReservacionController(ReservacionRepository r, AlojamientoRepository a, UserRepository u) {
        this.reservacionRepository = r;
        this.alojamientoRepository = a;
        this.userRepository = u;
    }

    @GetMapping("/usuario/{userId}")
    public List<Reservacion> get_reservation(@PathVariable Long userId) {
        return reservacionRepository.findByUserId(userId);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMisReservaciones(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body("No autenticado");
        }
        return userRepository.findByEmail(principal.getName())
                .map(user -> ResponseEntity.ok(reservacionRepository.findByUserId(user.getId())))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<Reservacion> get_all_reservation() {
        return reservacionRepository.findAll();
    }

    
    @PostMapping
    public ResponseEntity<?> create_reservation(@RequestBody Map<String, Object> payload) {
        try {
            
            Long userId        = Long.valueOf(payload.get("userId").toString());
            Long alojamientoId = Long.valueOf(payload.get("alojamientoId").toString());

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            Alojamiento alojamiento = alojamientoRepository.findById(alojamientoId)
                    .orElseThrow(() -> new RuntimeException("Alojamiento no encontrado"));

            double total = alojamiento.getPrecioPorDia() * 30;

            if (user.getCreditos() < total) {
                return ResponseEntity.status(402).body("Error: Créditos insuficientes.");
            }

            user.setCreditos(user.getCreditos() - total);
            userRepository.save(user);

            java.util.Optional<Reservacion> reservaExistente =
                    reservacionRepository.findByUser_IdAndAccommodation_Id(userId, alojamientoId);

            if (reservaExistente.isPresent()) {
                Reservacion reserva = reservaExistente.get();
                reserva.setFechaSalida(reserva.getFechaSalida().plusDays(30));
                reserva.setPrecioTotal(reserva.getPrecioTotal() + total);
                return ResponseEntity.ok(reservacionRepository.save(reserva));
            } else {
                LocalDate checkIn  = LocalDate.now();
                LocalDate checkOut = checkIn.plusDays(30);

                Reservacion nuevaReserva = new Reservacion();
                nuevaReserva.setUser(user);
                nuevaReserva.setAccommodation(alojamiento);
                nuevaReserva.setFechaEntrada(checkIn);
                nuevaReserva.setFechaSalida(checkOut);
                nuevaReserva.setPrecioTotal(total);
                return ResponseEntity.ok(reservacionRepository.save(nuevaReserva));
            }

        } catch (NullPointerException e) {
            return ResponseEntity.badRequest().body("Error: Faltan campos obligatorios (userId, alojamientoId).");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error inesperado: " + e.getMessage());
        }
    }
}
