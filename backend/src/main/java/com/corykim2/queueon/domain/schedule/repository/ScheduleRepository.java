package com.corykim2.queueon.domain.schedule.repository;

import com.corykim2.queueon.domain.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    @EntityGraph(attributePaths = "show")
    Optional<Schedule> findWithShowById(Long id);
}
