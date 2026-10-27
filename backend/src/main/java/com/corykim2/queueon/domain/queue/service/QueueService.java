package com.corykim2.queueon.domain.queue.service;

import com.corykim2.queueon.domain.queue.dto.QueueEntryResponse;
import com.corykim2.queueon.domain.queue.dto.QueueRankResponse;
import com.corykim2.queueon.domain.queue.dto.QueueStatus;
import com.corykim2.queueon.domain.queue.exception.BookingNotOpenException;
import com.corykim2.queueon.domain.queue.exception.NotInQueueException;
import com.corykim2.queueon.domain.schedule.entity.Schedule;
import com.corykim2.queueon.domain.schedule.repository.ScheduleRepository;
import com.corykim2.queueon.domain.show.entity.Show;
import com.corykim2.queueon.global.util.RedisKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QueueService {
    private final ScheduleRepository scheduleRepository;
    private final StringRedisTemplate redisTemplate;
    private final RedisScript<Long> queueEnterScript;

    @Value("${queue.capacity}")
    private int capacity;

    // QUEUE-01
    public QueueEntryResponse enter(Long userId, Long scheduleId, boolean rejoin) {
        // 1) 회차 확인 + 게이트 (오픈 전 / 예매 OFF면 거절)
        Schedule schedule = scheduleRepository.findWithShowById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회차"));
        Show show = schedule.getShow();

        if (LocalDateTime.now().isBefore(show.getBookingOpenAt()) || !show.isBookable()) {
            throw new BookingNotOpenException();
        }

        // 2) 이미 대기열에 있고 rejoin이 아니면 → 진입하지 않고 현재 순번 응답
        Long rank = redisTemplate.opsForZSet()
                .rank(RedisKeys.queue(scheduleId), String.valueOf(userId));
        if (rank != null && !rejoin) {
            return toResponse(rank, true);
        }

        // 3) 진입 Lua 실행 (번호 발급 + ZADD + ZRANK)
        Long newRank = redisTemplate.execute(
                queueEnterScript,
                List.of(RedisKeys.queue(scheduleId), RedisKeys.queueSeq(scheduleId)),
                String.valueOf(userId)
        );

        // 4) 진입 후 순번으로 응답
        return toResponse(newRank, false);
    }

    // 등수(0부터) → 응답
    private QueueEntryResponse toResponse(long rank, boolean alreadyInQueue) {
        QueueStatus status = rank < capacity ? QueueStatus.ADMITTED : QueueStatus.WAITING;
        return new QueueEntryResponse(
                status,          // status
                rank,       // myRank, 0부터 시작, 프론트에서 계산
                alreadyInQueue   // alreadyInQueue
        );
    }

    // QUEUE-02 순번 조회 (폴링)
    public QueueRankResponse getRank(Long userId, Long scheduleId) {
        Long rank = redisTemplate.opsForZSet()
                .rank(RedisKeys.queue(scheduleId), String.valueOf(userId));

        if (rank == null) {
            throw new NotInQueueException();
        }
        return new QueueRankResponse(statusOf(rank), rank);
    }

    private QueueEntryResponse toEntryResponse(long rank, boolean alreadyInQueue) {
        return new QueueEntryResponse(statusOf(rank), rank, alreadyInQueue);
    }

    // 입장 판정: 앞 capacity명이면 입장
    private QueueStatus statusOf(long rank) {
        return rank < capacity ? QueueStatus.ADMITTED : QueueStatus.WAITING;
    }
}
