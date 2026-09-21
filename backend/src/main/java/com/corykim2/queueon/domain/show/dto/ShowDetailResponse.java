package com.corykim2.queueon.domain.show.dto;

import com.corykim2.queueon.domain.schedule.dto.ScheduleResponse;
import com.corykim2.queueon.domain.show.entity.Show;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

//SHOW-02
@Getter
@Builder
public class ShowDetailResponse {
    private Long id;
    private String name;
    private int seatCount;
    private LocalDateTime bookingOpenAt;
    private boolean bookable;
    private List<ScheduleResponse> schedules;   // 회차 포함

    public static ShowDetailResponse from(Show show) {
        return ShowDetailResponse.builder()
                .id(show.getId())
                .name(show.getName())
                .seatCount(show.getSeatCount())
                .bookingOpenAt(show.getBookingOpenAt())
                .bookable(show.isBookable())
                .schedules(show.getSchedules().stream()
                        .map(ScheduleResponse::from)
                        .toList())
                .build();
    }
}