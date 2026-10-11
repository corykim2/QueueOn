package com.corykim2.queueon.domain.queue.exception;

public class BookingNotOpenException extends RuntimeException {
    public BookingNotOpenException() {
        super("예매가 열리지 않았습니다.");
    }
}