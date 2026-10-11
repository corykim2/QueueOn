package com.corykim2.queueon.domain.queue.service;

import com.corykim2.queueon.domain.queue.dto.QueueEntryResponse;
import com.corykim2.queueon.domain.queue.exception.BookingNotOpenException;
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

        // 2) 이미 대기열에 있는지
        Long rank = redisTemplate.opsForZSet()
                .rank(RedisKeys.queue(scheduleId), String.valueOf(userId));
        // TODO: 이미 있고 rejoin이 아니면 → 진입하지 않고 현재 순번으로 응답

        // 3) 진입 Lua 실행
        // TODO: 키 2개, ARGV 1개로 실행

        // 4) 순번 계산 + 입장 판정
        // TODO: rank로 myRank, aheadCount, status(capacity 비교) 만들어서 응답
        return null;
    }
}
