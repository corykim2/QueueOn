package com.corykim2.queueon.domain.show.dto;

import lombok.Getter;
import java.time.LocalDateTime;

//ADMIN-02
@Getter
public class ShowUpdateRequest {
    private String name;
    private int seatCount;
    private LocalDateTime bookingOpenAt;
}
