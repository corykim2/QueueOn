package com.corykim2.queueon.domain.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 자동 증가
    private Long id;

    @Enumerated(EnumType.STRING)                          // Enum을 문자열로 저장
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private LocalDateTime createdAt;                      // 생성일
}
