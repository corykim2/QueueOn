package com.corykim2.queueon.domain.reservation.service;

import com.corykim2.queueon.domain.reservation.dto.ConfirmResult;
import com.corykim2.queueon.domain.reservation.dto.SeatLayoutResponse;
import com.corykim2.queueon.domain.reservation.entity.Reservation;
import com.corykim2.queueon.domain.reservation.repository.ReservationRepository;
import com.corykim2.queueon.domain.schedule.entity.Schedule;
import com.corykim2.queueon.domain.schedule.repository.ScheduleRepository;
import com.corykim2.queueon.domain.user.repository.UserRepository;
import com.corykim2.queueon.global.util.RedisKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.corykim2.queueon.domain.reservation.dto.ConfirmResult.SEAT_UNAVAILABLE;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final StringRedisTemplate redisTemplate;
    private final RedisScript<Long> holdSeatScript; //Lua
    private final ReservationWriter reservationWriter;

    private static final long HOLD_TTL_SECONDS = 600; //TTL

    //RESV-01
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public boolean holdSeat(Long userId, Long scheduleId, int seatNumber) {
        // 1) 회차 존재 확인
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회차"));

        // 2) 좌석 번호 유효성 (1 ~ seatCount 범위인지)
        int seatCount = schedule.getShow().getSeatCount();
        if (seatNumber < 1 || seatNumber > seatCount) {
            throw new IllegalArgumentException("잘못된 좌석 번호");
        }

        // 3) 선점 + blocked 추가를 Lua로 원자적 실행
        Long result = redisTemplate.execute(
                holdSeatScript,
                List.of(RedisKeys.hold(scheduleId, seatNumber), RedisKeys.blocked(scheduleId)),
                String.valueOf(userId),
                String.valueOf(seatNumber),
                String.valueOf(HOLD_TTL_SECONDS)
        );

        // 4) 1 = 선점 성공, 0 = 이미 선점됨
        return Long.valueOf(1).equals(result);

    }

    //RESV-03
    public ConfirmResult confirmSeat(Long userId, Long scheduleId, int seatNumber) {
        // 1) 회차 확인 + 좌석 번호 유효성 (holdSeat과 동일)
        validateSeat(scheduleId,seatNumber);

        // 2) hold가 내 것인지 확인 (Redis)
        String holder = redisTemplate.opsForValue().get(RedisKeys.hold(scheduleId, seatNumber));
        if (!String.valueOf(userId).equals(holder)) {
            return judgeRejection(userId, scheduleId, seatNumber, holder);
        }

        // 3) MySQL 저장 (제약조건이 최종 보장)
        try {
            reservationWriter.save(userId, scheduleId, seatNumber);
        } catch (DataIntegrityViolationException e) {
            return judgeRejection(userId, scheduleId, seatNumber, holder);
        }

        // 4) Redis 반영 (hold 확정 전환)
        applyConfirmed(scheduleId, seatNumber);

        return ConfirmResult.CONFIRMED;
    }


    //RESV-03
    // 회차 존재 + 좌석 번호 유효성 검증
    private void validateSeat(Long scheduleId, int seatNumber) {
        Schedule schedule = scheduleRepository.findWithShowById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회차"));

        int seatCount = schedule.getShow().getSeatCount();
        if (seatNumber < 1 || seatNumber > seatCount) {
            throw new IllegalArgumentException("잘못된 좌석 번호");
        }
    }

    //RESV-03
    // 거절 이유 판단 (설계한 흐름: MySQL 먼저 → 없으면 hold 다시)
    private ConfirmResult judgeRejection(Long userId, Long scheduleId, int seatNumber, String holder) {
        // MySQL에 회차+좌석 예매가 있으면 → user 비교 → 내 것 / 남의 것
        Optional<Reservation> found = reservationRepository.findByScheduleIdAndSeatNumber(scheduleId, seatNumber);

        if (found.isPresent()) {
            Reservation reservation = found.get();
            if (userId.equals(reservation.getUser().getId())) {
                applyConfirmed(scheduleId, seatNumber);
                return ConfirmResult.ALREADY_CONFIRMED;
            }
        }
        // 예매 없음
        if (holder == null) {
            return ConfirmResult.HOLD_EXPIRED;
        }
        return ConfirmResult.SEAT_UNAVAILABLE;
    }

    //RESV-03
    // Redis에 확정 상태 반영: hold를 확정 표시로(TTL 제거) + blocked 보장
    private void applyConfirmed(Long scheduleId, int seatNumber) {
        redisTemplate.opsForValue().set(RedisKeys.hold(scheduleId, seatNumber), RedisKeys.CONFIRMED);
        redisTemplate.opsForSet().add(RedisKeys.blocked(scheduleId), String.valueOf(seatNumber));
    }
}