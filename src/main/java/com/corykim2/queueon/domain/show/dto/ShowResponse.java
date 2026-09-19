package com.corykim2.queueon.domain.show.dto;

import com.corykim2.queueon.domain.schedule.dto.ScheduleResponse;
import com.corykim2.queueon.domain.show.entity.Show;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ShowResponse {
    private Long id;
    private String name;
    private int seatCount;
    private LocalDateTime bookingOpenAt;
    private boolean isBookable;
    private List<ScheduleResponse> schedules;

    // 엔티티 → DTO 변환
    public static ShowResponse from(Show show) {
        ShowResponse response = new ShowResponse();
        response.id = show.getId();
        response.name = show.getName();
        response.seatCount = show.getSeatCount();
        response.bookingOpenAt = show.getBookingOpenAt();
        response.isBookable = show.isBookable();
        response.schedules = show.getSchedules()
                .stream()
                .map(ScheduleResponse::from)
                .toList();
        return response;
    }
}
