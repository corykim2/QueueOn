package com.corykim2.queueon.domain.queue.controller;

import com.corykim2.queueon.domain.queue.dto.QueueEntryResponse;
import com.corykim2.queueon.domain.queue.dto.QueueRankResponse;
import com.corykim2.queueon.domain.queue.exception.NotInQueueException;
import com.corykim2.queueon.domain.queue.service.QueueService;
import com.corykim2.queueon.domain.reservation.dto.ErrorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    // QUEUE-02 순번 조회 (폴링)
    @GetMapping
    public ResponseEntity<QueueRankResponse> getRank(
            @PathVariable Long scheduleId,
            @RequestHeader("X-User-Id") Long userId
    ) {
        return ResponseEntity.ok(queueService.getRank(userId, scheduleId));
    }

    @ExceptionHandler(NotInQueueException.class)
    public ResponseEntity<ErrorResponse> handleNotInQueue(NotInQueueException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("NOT_IN_QUEUE", e.getMessage()));
    }
}