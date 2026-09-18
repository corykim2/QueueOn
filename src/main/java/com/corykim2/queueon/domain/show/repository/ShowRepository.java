package com.corykim2.queueon.domain.show.repository;

import com.corykim2.queueon.domain.show.entity.Show;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShowRepository extends JpaRepository<Show, Long> {
}
