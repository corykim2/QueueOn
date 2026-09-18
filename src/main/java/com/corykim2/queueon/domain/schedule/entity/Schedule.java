package com.corykim2.queueon.domain.schedule.entity;

import com.corykim2.queueon.domain.show.entity.Show;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "schedules")
@Getter
@NoArgsConstructor
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)       // 회차 N : 공연 1
    @JoinColumn(name = "show_id", nullable = false)
    private Show show;                         // 소속 공연

    @Column(nullable = false)
    private LocalDateTime performanceDate;     // 공연 시간(회차 일시)
}
