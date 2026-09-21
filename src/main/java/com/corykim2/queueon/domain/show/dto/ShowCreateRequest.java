package com.corykim2.queueon.domain.show.dto;

import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;

//ADMIN-01
@Getter
public class ShowCreateRequest {
    private String name;              // 공연 이름
    private int seatCount;            // 좌석 수
    private LocalDateTime bookingOpenAt;  // 예매 오픈 시간
    private List<LocalDateTime> schedules;        // 회차 목록 ← 배열을 List로
}