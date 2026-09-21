package com.corykim2.queueon.domain.show.dto;

import com.corykim2.queueon.domain.schedule.dto.ScheduleResponse;
import com.corykim2.queueon.domain.show.entity.Show;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class AdminShowResponse {
    private Long id;
    private String name;
    private int seatCount;
    private LocalDateTime bookingOpenAt;
    private boolean bookable;
    private List<ScheduleResponse> schedules;

    public static AdminShowResponse from(Show show) {
        return AdminShowResponse.builder()
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
