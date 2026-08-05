package com.reviewservice.controller;

import com.reviewservice.model.dto.ReviewRequestDto;
import com.reviewservice.model.dto.ReviewResponseDto;
import com.reviewservice.service.ReviewService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private static final Logger log = LoggerFactory.getLogger(ReviewController.class);

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<ReviewResponseDto>> getByRoom(@PathVariable UUID roomId) {
        return ResponseEntity.ok(reviewService.getReviewsByRoom(roomId));
    }

    @GetMapping("/room/{roomId}/average")
    public ResponseEntity<Double> getAverageRating(@PathVariable UUID roomId) {
        return ResponseEntity.ok(reviewService.getAverageRatingForRoom(roomId));
    }

    @PostMapping
    public ResponseEntity<ReviewResponseDto> create(@Valid @RequestBody ReviewRequestDto dto) {
        log.info("Received request to create review for room {}", dto.getRoomId());
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.createReview(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReviewResponseDto> update(@PathVariable UUID id,
                                                     @Valid @RequestBody ReviewRequestDto dto) {
        log.info("Received request to update review {}", id);
        return ResponseEntity.ok(reviewService.updateReview(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        log.info("Received request to delete review {}", id);
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }
}
