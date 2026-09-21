package com.corykim2.queueon.domain.schedule.repository;

import com.corykim2.queueon.domain.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    void deleteByShowId(Long showId);   // 이 공연의 회차 전부 삭제
}
