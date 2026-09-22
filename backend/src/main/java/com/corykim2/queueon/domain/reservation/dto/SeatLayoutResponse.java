package com.corykim2.queueon.domain.reservation.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

//RESV-01
@Getter
@Builder
public class SeatLayoutResponse {
    private Long scheduleId;
    private int seatCount;
    private List<Integer> blockedSeats;   // 막힌 좌석 번호들 (판매+선점 통합)
}