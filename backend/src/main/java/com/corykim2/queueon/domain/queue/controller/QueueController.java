package com.corykim2.queueon.domain.queue.controller;

import com.corykim2.queueon.domain.queue.dto.QueueEntryResponse;
import com.corykim2.queueon.domain.queue.service.QueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/schedules/{scheduleId}/queue")
@RequiredArgsConstructor
public class QueueController {

    private final QueueService queueService;

    // QUEUE-01 대기열 진입
    @PostMapping
    public ResponseEntity<QueueEntryResponse> enter(
            @PathVariable Long scheduleId,
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(defaultValue = "false") boolean rejoin
    ) {
        return ResponseEntity.ok(queueService.enter(userId, scheduleId, rejoin));
    }
}