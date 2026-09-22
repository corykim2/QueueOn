package com.corykim2.queueon.domain.reservation.service;

import com.corykim2.queueon.domain.reservation.dto.SeatLayoutResponse;
import com.corykim2.queueon.domain.schedule.entity.Schedule;
import com.corykim2.queueon.domain.schedule.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        Set<String> blocked = redisTemplate.opsForSet().members(blockedKey(scheduleId));

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

    // key 생성은 한곳에 모으기 (강의: key 규칙 흩뿌리면 오타 지옥)
    private String blockedKey(Long scheduleId) {
        return "blocked:" + scheduleId;
    }
}