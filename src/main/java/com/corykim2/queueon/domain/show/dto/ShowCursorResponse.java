package com.corykim2.queueon.domain.show.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

//SHOW-01
@Getter
@AllArgsConstructor
public class ShowCursorResponse {
    private List<ShowResponse> shows;   // 공연 목록
    private Long nextCursor;             // 다음 요청 때 쓸 커서 (마지막 공연 id)
    private boolean hasNext;             // 더 있는지
}