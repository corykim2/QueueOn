package com.corykim2.queueon.domain.show.service;

import com.corykim2.queueon.domain.schedule.entity.Schedule;
import com.corykim2.queueon.domain.schedule.repository.ScheduleRepository;
import com.corykim2.queueon.domain.show.dto.ShowCreateRequest;
import com.corykim2.queueon.domain.show.dto.ShowResponse;
import com.corykim2.queueon.domain.show.dto.ShowUpdateRequest;
import com.corykim2.queueon.domain.show.entity.Show;
import com.corykim2.queueon.domain.show.repository.ShowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Transactional
    public Long updateShow(Long showId, ShowUpdateRequest request){
        // 1. 수정할 공연 찾기 (없으면 예외)
        Show show = showRepository.findById(showId)
                .orElseThrow(() -> new IllegalArgumentException("공연을 찾을 수 없습니다"));

        // 2. 값 변경
        show.update(request.getName(), request.getSeatCount(), request.getBookingOpenAt());

        // 3. id 반환
        return show.getId();
    }

    @Transactional
    public void deleteShow(Long showId) {
        Show show = showRepository.findById(showId)
                .orElseThrow(() -> new IllegalArgumentException("공연을 찾을 수 없습니다"));

        show.softDelete();
    }

    @Transactional(readOnly = true)
    public List<ShowResponse> getShows() {
        return showRepository.findByIsDeletedFalse()   // 삭제 안 된 것 조회
                .stream()
                .map(ShowResponse::from)                // 각 Show를 DTO로 변환
                //.map(show -> ShowResponse.from(show))  이거랑 같음
                .toList();
        //이거 이렇게 만들지 않고, for each 써도 똑같은데 이러면 좀 짧아져서 편함
    }
}
