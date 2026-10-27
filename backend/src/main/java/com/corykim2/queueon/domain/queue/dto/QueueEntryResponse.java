package com.corykim2.queueon.domain.queue.dto;

// 진입 응답 (POST /queue)
public record QueueEntryResponse(
        QueueStatus status,
        long rank,               // 0부터 시작하는 등수 (프론트가 +1, 앞 사람 수 계산)
        boolean alreadyInQueue
) {
}