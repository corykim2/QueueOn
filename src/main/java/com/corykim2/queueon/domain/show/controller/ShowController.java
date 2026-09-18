package com.corykim2.queueon.domain.show.controller;

import com.corykim2.queueon.domain.show.dto.ShowCreateRequest;
import com.corykim2.queueon.domain.show.service.ShowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/shows")
@RequiredArgsConstructor
public class ShowController {
    private final ShowService showService;

    @PostMapping
    public ResponseEntity<Long> createShow(@RequestBody ShowCreateRequest request) {
        Long showId = showService.createShow(request);
        return ResponseEntity.status(201).body(showId);
    }
}
