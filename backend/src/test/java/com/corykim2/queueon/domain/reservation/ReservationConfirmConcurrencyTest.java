package com.corykim2.queueon.domain.reservation;

import com.corykim2.queueon.domain.reservation.repository.ReservationRepository;
import com.corykim2.queueon.domain.reservation.dto.ConfirmResult;
import com.corykim2.queueon.domain.reservation.service.ReservationService;
import com.corykim2.queueon.global.util.RedisKeys;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ReservationConfirmConcurrencyTest {

    @Autowired ReservationService reservationService;
    @Autowired ReservationRepository reservationRepository;
    @Autowired StringRedisTemplate redisTemplate;

    @Test
    void 같은_유저_동시_확정_중복_저장_0건() throws InterruptedException {
        // ── 준비 ──
        int threadCount = 10;
        Long userId = 1L;          // ← 실제 users id로
        Long scheduleId = 1L;      // ← 실제 schedules id로
        int seatNumber = 5;

        // 이전 실행 흔적 정리 (Redis + MySQL)
        redisTemplate.delete(RedisKeys.hold(scheduleId, seatNumber));
        redisTemplate.delete(RedisKeys.blocked(scheduleId));
        reservationRepository.findByScheduleIdAndSeatNumber(scheduleId, seatNumber)
                .ifPresent(reservationRepository::delete);

        // 먼저 선점해두기
        assertThat(reservationService.holdSeat(userId, scheduleId, seatNumber)).isTrue();

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);

        // 결과별 개수 + 예외 개수
        Map<ConfirmResult, AtomicInteger> results = new ConcurrentHashMap<>();
        AtomicInteger errorCount = new AtomicInteger(0);

        // ── 같은 유저가 동시에 확정 ──
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await();
                    ConfirmResult result = reservationService.confirmSeat(userId, scheduleId, seatNumber);
                    results.computeIfAbsent(result, k -> new AtomicInteger()).incrementAndGet();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    errorCount.incrementAndGet();
                    System.out.println("예외: " + e.getClass().getSimpleName() + " - " + e.getMessage());
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        doneLatch.await();
        executor.shutdown();

        // ── 결과 출력 ──
        System.out.println("결과: " + results + ", 예외: " + errorCount.get());

        // ── 검증 ──
        assertThat(reservationRepository.findByScheduleIdAndSeatNumber(scheduleId, seatNumber))
                .isPresent();                                                   // 예매는 저장됨
        assertThat(results.getOrDefault(ConfirmResult.CONFIRMED, new AtomicInteger()).get())
                .isEqualTo(1);                                                  // 확정은 딱 1번
        assertThat(errorCount.get()).isEqualTo(0);                              // 예외 없이
        assertThat(redisTemplate.opsForValue().get(RedisKeys.hold(scheduleId, seatNumber)))
                .isEqualTo(RedisKeys.CONFIRMED);                                // Redis도 확정 상태
    }
}