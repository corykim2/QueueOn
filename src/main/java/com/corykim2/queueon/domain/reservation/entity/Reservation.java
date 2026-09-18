package com.corykim2.queueon.domain.reservation.entity;

import com.corykim2.queueon.domain.schedule.entity.Schedule;
import com.corykim2.queueon.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reservations")
@Getter
@NoArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;                 // 어느 회차 예매인지

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;                          // 누가 예매했는지

    @Column(nullable = false)
    private int seatNumber;                     // 좌석 번호
}