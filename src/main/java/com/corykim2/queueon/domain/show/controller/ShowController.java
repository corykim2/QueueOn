package com.corykim2.queueon.domain.show.controller;

import com.corykim2.queueon.domain.show.dto.ShowCreateRequest;
import com.corykim2.queueon.domain.show.dto.ShowResponse;
import com.corykim2.queueon.domain.show.dto.ShowUpdateRequest;
import com.corykim2.queueon.domain.show.service.ShowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/shows")
@RequiredArgsConstructor
public class ShowController {
    private final ShowService showService;

    //ADMIN-01
    @PostMapping
    public ResponseEntity<Long> createShow(@RequestBody ShowCreateRequest request) {
        Long showId = showService.createShow(request);
        return ResponseEntity.status(201).body(showId);
    }

    //ADMIN-02
    @PutMapping("/{showId}")
    public ResponseEntity<Long> updateShow(
            @PathVariable Long showId,
            @RequestBody ShowUpdateRequest request) {
        Long updatedId = showService.updateShow(showId, request);
        return ResponseEntity.ok(updatedId);
    }

    //ADMIN-03
    @DeleteMapping("/{showId}")
    public ResponseEntity<Void> deleteShow(@PathVariable Long showId) {
        showService.deleteShow(showId);
        return ResponseEntity.noContent().build();  // 204
    }

    //ADMIN-04
    @GetMapping
    public ResponseEntity<List<ShowResponse>> getShows() {
        return ResponseEntity.ok(showService.getShows());
    }
}
