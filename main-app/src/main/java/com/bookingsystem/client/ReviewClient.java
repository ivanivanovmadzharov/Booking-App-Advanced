package com.bookingsystem.client;

import com.bookingsystem.model.dto.ReviewDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "review-service", url = "${review.service.url}")
public interface ReviewClient {

    @GetMapping("/api/reviews/room/{roomId}")
    List<ReviewDto> getReviewsByRoom(@PathVariable UUID roomId);

    @GetMapping("/api/reviews/room/{roomId}/average")
    Double getAverageRating(@PathVariable UUID roomId);

    @PostMapping("/api/reviews")
    ReviewDto createReview(@RequestBody ReviewDto reviewDto);

    @PutMapping("/api/reviews/{id}")
    ReviewDto updateReview(@PathVariable UUID id, @RequestBody ReviewDto reviewDto);

    @DeleteMapping("/api/reviews/{id}")
    void deleteReview(@PathVariable UUID id);
}
