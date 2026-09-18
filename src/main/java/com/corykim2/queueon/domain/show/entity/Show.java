package com.corykim2.queueon.domain.show.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "shows")
@Getter
@NoArgsConstructor
public class Show {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 자동 증가
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;                    // 공연 이름

    @Column(nullable = false)
    private int seatCount;                   // 좌석 수

    @Column(nullable = false)
    private LocalDateTime bookingOpenAt;     // 예매 오픈 시간

    @Column(nullable = false)
    private boolean isBookable = false;       // 예매 가능 플래그 (기본 false)

    @Column(nullable = false)
    private LocalDateTime createdAt;         // 생성일

    @Column(nullable = false)
    private LocalDateTime updatedAt;         // 수정일
}
