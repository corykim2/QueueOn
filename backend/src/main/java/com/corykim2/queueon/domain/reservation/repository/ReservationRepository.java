package com.corykim2.queueon.domain.reservation.repository;

import com.corykim2.queueon.domain.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    //RESV-03
    Optional<Reservation> findByScheduleIdAndSeatNumber(Long scheduleId, int seatNumber);
}
