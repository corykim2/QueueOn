package com.corykim2.queueon.domain.reservation.controller;

import com.corykim2.queueon.domain.reservation.dto.SeatLayoutResponse;
import com.corykim2.queueon.domain.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    //RESV-02
    @PostMapping("/{seatNumber}/hold")
    public ResponseEntity<Void> holdSeat(
            @PathVariable Long scheduleId,
            @PathVariable int seatNumber,
            @RequestHeader("X-User-Id") Long userId) {   // TODO: 로그인 도입 시 세션에서 (문서 8번)

        boolean success = reservationService.holdSeat(userId, scheduleId, seatNumber);

        if (success) {
            return ResponseEntity.status(HttpStatus.CREATED).build();    // 201 선점 성공
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();   // 409 이미 선점/판매
        }
    }
}