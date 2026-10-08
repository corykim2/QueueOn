package com.corykim2.queueon.domain.reservation.entity;

import com.corykim2.queueon.domain.schedule.entity.Schedule;
import com.corykim2.queueon.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

// 나중에 soft delete 추가하게 되면 새로 테이블 하나 만들어서 삭제하면 옮기는 방식으로 구현
@Entity
@Table(
        name = "reservations",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_reservation_schedule_seat",
                columnNames = {"schedule_id", "seat_number"}
        )
)
@Getter
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
@Builder
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