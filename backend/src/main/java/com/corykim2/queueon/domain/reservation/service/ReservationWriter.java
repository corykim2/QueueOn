package com.corykim2.queueon.domain.reservation.service;

import com.corykim2.queueon.domain.reservation.entity.Reservation;
import com.corykim2.queueon.domain.reservation.repository.ReservationRepository;
import com.corykim2.queueon.domain.schedule.repository.ScheduleRepository;
import com.corykim2.queueon.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

// 예매 저장만 담당. 이 메서드 하나가 독립된 트랜잭션.
@Component
@RequiredArgsConstructor
public class ReservationWriter {

    private final ReservationRepository reservationRepository;
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;

    @Transactional
    public void save(Long userId, Long scheduleId, int seatNumber) {
        Reservation reservation = Reservation.builder()
                .schedule(scheduleRepository.getReferenceById(scheduleId))
                .user(userRepository.getReferenceById(userId))
                .seatNumber(seatNumber)
                .build();
        reservationRepository.save(reservation);
    }
}