package com.corykim2.queueon.domain.queue.dto;

public record QueueEntryResponse(
        QueueStatus status,
        long myRank,          // 내 순번 (1부터)
        long aheadCount,      // 내 앞 대기 인원
        boolean alreadyInQueue
) {
}