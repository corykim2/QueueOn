package com.corykim2.queueon.domain.reservation.repository;

import com.corykim2.queueon.domain.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
