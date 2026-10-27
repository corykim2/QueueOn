package com.corykim2.queueon.domain.queue.dto;

// 순번 조회 응답 (GET /queue, 폴링용)
public record QueueRankResponse(
        QueueStatus status,
        long rank
) {
}