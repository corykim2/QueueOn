package com.corykim2.queueon.domain.reservation.controller;

import com.corykim2.queueon.domain.reservation.dto.SeatLayoutResponse;
import com.corykim2.queueon.domain.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/schedules/{scheduleId}/seats")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    //RESV-01
    @GetMapping
    public SeatLayoutResponse getSeatLayout(@PathVariable Long scheduleId) {
        return reservationService.getSeatLayout(scheduleId);
    }
}