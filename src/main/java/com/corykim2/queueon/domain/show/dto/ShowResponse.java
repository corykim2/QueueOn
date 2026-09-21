package com.corykim2.queueon.domain.show.dto;

import com.corykim2.queueon.domain.show.entity.Show;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

//SHOW-01
@Getter
@Builder
public class ShowResponse {
    private Long id;
    private String name;
    private int seatCount;
    private LocalDateTime bookingOpenAt;
    private boolean bookable;

    public static ShowResponse from(Show show) {
        return ShowResponse.builder()
                .id(show.getId())
                .name(show.getName())
                .seatCount(show.getSeatCount())
                .bookingOpenAt(show.getBookingOpenAt())
                .bookable(show.isBookable())
                .build();
    }
}