package com.corykim2.queueon.domain.show.service;

import com.corykim2.queueon.domain.schedule.entity.Schedule;
import com.corykim2.queueon.domain.schedule.repository.ScheduleRepository;
import com.corykim2.queueon.domain.show.dto.ShowCreateRequest;
import com.corykim2.queueon.domain.show.entity.Show;
import com.corykim2.queueon.domain.show.repository.ShowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ShowService {
    private final ShowRepository showRepository;
    private final ScheduleRepository scheduleRepository;

    @Transactional
    public Long createShow(ShowCreateRequest request) {
        // 1. 공연(Show) 엔티티 만들기
        Show show = Show.builder()
                .name(request.getName())
                .seatCount(request.getSeatCount())
                .bookingOpenAt(request.getBookingOpenAt())
                .isBookable(false)                    // 생성 시 예매불가(기본)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // 2. 공연 저장
        Show savedShow = showRepository.save(show);

        // 3. 회차(Schedule)들 만들어서 저장
        for (LocalDateTime date : request.getSchedules()) {
            Schedule schedule = Schedule.builder()
                    .show(savedShow)                  // 저장된 공연과 연결
                    .performanceDate(date)
                    .build();
            scheduleRepository.save(schedule);
        }

        // 4. 만들어진 공연 id 반환
        return savedShow.getId();
    }
}
