package com.corykim2.queueon.domain.reservation.service;

import com.corykim2.queueon.domain.reservation.dto.SeatLayoutResponse;
import com.corykim2.queueon.domain.schedule.entity.Schedule;
import com.corykim2.queueon.domain.schedule.repository.ScheduleRepository;
import com.corykim2.queueon.global.util.RedisKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) //여기에 달면 각각에 달린 효과
public class ReservationService {
    private final ScheduleRepository scheduleRepository;
    private final StringRedisTemplate redisTemplate;

    //RESV-01
    public SeatLayoutResponse getSeatLayout(Long scheduleId) {
        // 1) 회차 확인 + 좌석 수 (MySQL)
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회차"));
        int seatCount = schedule.getShow().getSeatCount();

        // 2) 막힌 좌석 읽기 (Redis Set)  →  SMEMBERS blocked:{id}
        //    members()가 돌려주는 건 좌석번호 문자열들의 Set. 예: {"2","7"}
        Set<String> blocked = redisTemplate.opsForSet().members(RedisKeys.blocked(scheduleId));

        // 3) 문자열 Set → 숫자 List 변환 (없으면 빈 리스트)
        List<Integer> blockedSeats = (blocked == null)
                ? List.of()
                : blocked.stream().map(Integer::parseInt).toList();

        return SeatLayoutResponse.builder()
                .scheduleId(scheduleId)
                .seatCount(seatCount)
                .blockedSeats(blockedSeats)
                .build();
    }

    //RESV-02
    public boolean holdSeat(Long userId, Long scheduleId, int seatNumber) {
        // 1) 회차 존재 확인
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회차"));

        // 2) 좌석 번호 유효성 (1 ~ seatCount 범위인지)
        int seatCount = schedule.getShow().getSeatCount();
        if (seatNumber < 1 || seatNumber > seatCount) {
            throw new IllegalArgumentException("잘못된 좌석 번호");
        }

        // 3) 선점 redis에 넣기
        Boolean result = redisTemplate.opsForValue()
                .setIfAbsent(RedisKeys.hold(scheduleId, seatNumber), String.valueOf(userId), Duration.ofSeconds(600));

        // 4) 선점 redis set에 넣기
        if (Boolean.TRUE.equals(result)){
            redisTemplate.opsForSet().add(RedisKeys.blocked(scheduleId), String.valueOf(seatNumber));
            return true;
        }
        else{ return false; }

    }
}