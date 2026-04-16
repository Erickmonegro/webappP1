package com.webapp.webappstudents.repository;

import com.webapp.webappstudents.model.Reservacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservacionRepository extends JpaRepository<Reservacion, Long> {
    List<Reservacion> findByUserId(Long userid);
    Optional<Reservacion> findByUser_IdAndAccommodation_Id(Long userId, Long accommodationId);
}

