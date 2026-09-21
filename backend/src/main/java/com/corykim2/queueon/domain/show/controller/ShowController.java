package com.corykim2.queueon.domain.show.controller;

import com.corykim2.queueon.domain.show.dto.ShowCursorResponse;
import com.corykim2.queueon.domain.show.dto.ShowDetailResponse;
import com.corykim2.queueon.domain.show.service.ShowService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/shows")
@RequiredArgsConstructor
public class ShowController {
    private final ShowService showService;

    //SHOW-01
    @GetMapping
    public ShowCursorResponse getShows(
            @RequestParam(required = false) Long cursor) {
        return showService.getShows(cursor);
    }

    //SHOW-02
    @GetMapping("/{showId}")
    public ShowDetailResponse getShow(@PathVariable Long showId) {
        return showService.getShow(showId);
    }
}
