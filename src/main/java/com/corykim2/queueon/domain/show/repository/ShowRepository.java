package com.corykim2.queueon.domain.show.repository;

import com.corykim2.queueon.domain.show.entity.Show;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShowRepository extends JpaRepository<Show, Long> {
    @EntityGraph(attributePaths = "schedules") //스케쥴도 fetch join으로 한 번에 들고옴
    List<Show> findByIsDeletedFalse();
}
