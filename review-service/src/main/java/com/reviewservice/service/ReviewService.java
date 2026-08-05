package com.reviewservice.service;

import com.reviewservice.model.dto.ReviewRequestDto;
import com.reviewservice.model.dto.ReviewResponseDto;

import java.util.List;
import java.util.UUID;

public interface ReviewService {

    ReviewResponseDto createReview(ReviewRequestDto dto);

    ReviewResponseDto updateReview(UUID id, ReviewRequestDto dto);

    void deleteReview(UUID id);

    List<ReviewResponseDto> getReviewsByRoom(UUID roomId);

    double getAverageRatingForRoom(UUID roomId);

    void flagOldLowRatedReviews();

    void deleteFlaggedReviews();
}
