package com.corykim2.queueon.global.util;


// key 생성은 한곳에 모으기
public class RedisKeys {
    private RedisKeys() {} // 인스턴스 못 만들게 (유틸 클래스)

    //RESV-01
    public static String blocked(Long scheduleId) {
        return "blocked:" + scheduleId;
    }

    //RESV-02
    public static String hold(Long scheduleId, int seatNumber) {
        return "hold:" + scheduleId + ":" + seatNumber;
    }
}
