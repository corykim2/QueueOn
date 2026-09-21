package com.corykim2.queueon.domain.show.service;

import com.corykim2.queueon.domain.schedule.entity.Schedule;
import com.corykim2.queueon.domain.schedule.repository.ScheduleRepository;
import com.corykim2.queueon.domain.show.dto.*;
import com.corykim2.queueon.domain.show.entity.Show;
import com.corykim2.queueon.domain.show.repository.ShowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShowService {
    private final ShowRepository showRepository;
    private final ScheduleRepository scheduleRepository;

    //ADMIN-01
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

    //ADMIN-02
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

    //ADMIN-03
    @Transactional
    public void deleteShow(Long showId) {
        Show show = showRepository.findById(showId)
                .orElseThrow(() -> new IllegalArgumentException("공연을 찾을 수 없습니다"));

        show.softDelete();
    }

    //ADMIN-04
    @Transactional(readOnly = true)
    public List<AdminShowResponse> getShowsForAdmin() {
        return showRepository.findByIsDeletedFalse()   // 삭제 안 된 것 조회
                .stream()
                .map(AdminShowResponse::from)                // 각 Show를 DTO로 변환
                //.map(show -> AdminShowResponse.from(show))  이거랑 같음
                .toList();
        //이거 이렇게 만들지 않고, for each 써도 똑같은데 이러면 좀 짧아져서 편함
    }

    //SHOW-01
    @Transactional(readOnly = true)
    public ShowCursorResponse getShows(Long cursor) {
        // 1. 첫 요청이면 커서를 최댓값으로
        if (cursor == null) {
            cursor = Long.MAX_VALUE;
        }

        // 2. Slice로 조회 (개수 제한만, 페이지는 0 고정)
        Pageable pageable = PageRequest.of(0, 20);
        Slice<Show> slice = showRepository
                .findByIdLessThanAndIsDeletedFalseOrderByIdDesc(cursor, pageable);

        // 3. 목록 꺼내서 DTO 변환
        List<ShowResponse> shows = slice.getContent().stream()
                .map(ShowResponse::from)
                .toList();

        // 4. hasNext (Slice가 판단해줌)
        boolean hasNext = slice.hasNext();

        // 5. nextCursor 계산 (목록이 비어있지 않으면 마지막 공연 id)
        Long nextCursor = shows.isEmpty()
                ? null
                : shows.get(shows.size() - 1).getId();

        // 6. 응답 조립
        return new ShowCursorResponse(shows, nextCursor, hasNext);
    }

    //SHOW-02
    @Transactional(readOnly = true)
    public ShowDetailResponse getShow(Long showId) {
        Show show = showRepository.findWithSchedulesByIdAndIsDeletedFalse(showId)
                .orElseThrow(() -> new IllegalArgumentException("공연을 찾을 수 없습니다."));

        return ShowDetailResponse.from(show);
    }
}
