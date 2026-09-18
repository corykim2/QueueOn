package com.corykim2.queueon.domain.user.repository;
import com.corykim2.queueon.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
