package com.corykim2.queueon.domain.reservation;

import com.corykim2.queueon.domain.reservation.service.ReservationService;
import com.corykim2.queueon.global.util.RedisKeys;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest   // 실제 스프링 + Redis 붙여서 통합 테스트
public class ReservationConcurrencyTest {
    //실제 service 연결
    @Autowired
    ReservationService reservationService;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Test
    void 같은_좌석_동시_선점_중복_0건() throws InterruptedException {

        // ── 준비 ──
        int threadCount = 100;                    // 100명이 동시에
        Long scheduleId = 1L;                     // 회차 1
        int seatNumber = 3;                       // 좌석 3번 (모두 같은 좌석!)

        redisTemplate.delete(RedisKeys.hold(scheduleId, seatNumber));   // hold:1:3 삭제
        redisTemplate.delete(RedisKeys.blocked(scheduleId));            // blocked:1 삭제

        ExecutorService executor = Executors.newFixedThreadPool(threadCount); // 스레드 100개
        CountDownLatch startLatch = new CountDownLatch(1);        // 출발 신호탄
        CountDownLatch doneLatch = new CountDownLatch(threadCount); // 다 끝날때까지 대기용

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // ── 100개 스레드 준비 ──
        for (int i = 0; i < threadCount; i++) {
            long userId = i;
            executor.submit(() -> {
                try {
                    startLatch.await();   // 출발 신호까지 대기

                    boolean success = reservationService.holdSeat(userId, scheduleId, seatNumber);
                    if (success) {
                        successCount.incrementAndGet();
                    } else {
                        failCount.incrementAndGet();
                    }

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                } catch (Exception e) {          // 추가
                    e.printStackTrace();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        // 테스트 시작
        startLatch.countDown();   // "땅!" → 100개 일제히 holdSeat 돌진
        doneLatch.await();        // 100개 다 끝날 때까지 대기
        executor.shutdown();

        // 테스트 검증
        System.out.println("성공: " + successCount.get() + ", 실패: " + failCount.get());

        assertThat(successCount.get()).isEqualTo(1);              // 딱 1명만 성공
        assertThat(failCount.get()).isEqualTo(threadCount - 1);  // 나머지 99명 실패
        assertThat(redisTemplate.opsForSet().size(RedisKeys.blocked(scheduleId)))
                .isEqualTo(1L);                                  // blocked엔 좌석 1개만
    }
}