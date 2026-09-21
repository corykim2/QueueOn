package com.corykim2.queueon.domain.show.repository;

import com.corykim2.queueon.domain.show.entity.Show;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShowRepository extends JpaRepository<Show, Long> {
    //ADMIN-04
    @EntityGraph(attributePaths = "schedules") //스케쥴도 fetch join으로 한 번에 들고옴
    List<Show> findByIsDeletedFalse();

    //SHOW-01
    //Slice는 페이징할 때 count 없이 내부적으로 한개 더 조회함. -> hasNext()로 다음 거 체크 지원
    Slice<Show> findByIdLessThanAndIsDeletedFalseOrderByIdDesc(Long cursor, Pageable pageable);
}
