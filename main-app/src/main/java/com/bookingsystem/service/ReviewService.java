package com.bookingsystem.service;

import com.bookingsystem.model.dto.ReviewDto;

import java.util.List;
import java.util.UUID;

public interface ReviewService {

    List<ReviewDto> getReviewsForRoom(UUID roomId);

    Double getAverageRating(UUID roomId);

    ReviewDto submitReview(ReviewDto dto);

    ReviewDto updateReview(UUID id, ReviewDto dto);

    void deleteReview(UUID id);
}
