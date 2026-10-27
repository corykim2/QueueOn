package com.corykim2.queueon.domain.queue.exception;

public class NotInQueueException extends RuntimeException {
    public NotInQueueException() {
        super("대기열에 없습니다. 다시 진입해 주세요.");
    }
}