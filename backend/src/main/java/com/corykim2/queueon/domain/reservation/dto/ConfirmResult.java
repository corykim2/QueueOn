package com.corykim2.queueon.domain.reservation.dto;

public enum ConfirmResult {
    CONFIRMED,          // 201: 새로 확정됨
    ALREADY_CONFIRMED,  // 200: 이미 내가 확정함
    SEAT_UNAVAILABLE,   // 409: 남의 좌석
    HOLD_EXPIRED        // 409: 내 선점이 만료됨
}