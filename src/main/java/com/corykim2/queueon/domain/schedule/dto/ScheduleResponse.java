package com.corykim2.queueon.domain.schedule.dto;

import com.corykim2.queueon.domain.schedule.entity.Schedule;
import lombok.Getter;

import java.time.LocalDateTime;

//ADMIN-04
@Getter
public class ScheduleResponse {
    private Long id;
    private LocalDateTime performanceDate;

    public static ScheduleResponse from(Schedule schedule) {
        ScheduleResponse response = new ScheduleResponse();
        response.id = schedule.getId();
        response.performanceDate = schedule.getPerformanceDate();
        return response;
    }
}