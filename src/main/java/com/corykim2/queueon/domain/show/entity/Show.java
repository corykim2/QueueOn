package com.corykim2.queueon.domain.show.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(name = "shows")
@Getter
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
@Builder
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

    public void update(String name, int seatCount, LocalDateTime bookingOpenAt) {
        this.name = name;
        this.seatCount = seatCount;
        this.bookingOpenAt = bookingOpenAt;
        this.updatedAt = LocalDateTime.now();   // 수정일 갱신
    }
}