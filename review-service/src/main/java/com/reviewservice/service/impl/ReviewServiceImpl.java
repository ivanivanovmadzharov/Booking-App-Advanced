package com.reviewservice.service.impl;

import com.reviewservice.exception.ReviewNotFoundException;
import com.reviewservice.model.dto.ReviewRequestDto;
import com.reviewservice.model.dto.ReviewResponseDto;
import com.reviewservice.model.entity.Review;
import com.reviewservice.repository.ReviewRepository;
import com.reviewservice.service.ReviewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ReviewServiceImpl implements ReviewService {

    private static final Logger log = LoggerFactory.getLogger(ReviewServiceImpl.class);

    private final ReviewRepository reviewRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Override
    public ReviewResponseDto createReview(ReviewRequestDto dto) {
        Review review = new Review();
        review.setRoomId(dto.getRoomId());
        review.setGuestUsername(dto.getGuestUsername());
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());
        review.setCreatedAt(LocalDateTime.now());
        Review saved = reviewRepository.save(review);
        log.info("Review created for room {} by {}", dto.getRoomId(), dto.getGuestUsername());
        return toDto(saved);
    }

    @Override
    public ReviewResponseDto updateReview(UUID id, ReviewRequestDto dto) {
        Review review = findEntityById(id);
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());
        Review saved = reviewRepository.save(review);
        log.info("Review {} updated by {}", id, dto.getGuestUsername());
        return toDto(saved);
    }

    @Override
    public void deleteReview(UUID id) {
        Review review = findEntityById(id);
        reviewRepository.delete(review);
        log.info("Review {} deleted", id);
    }

    @Override
    public List<ReviewResponseDto> getReviewsByRoom(UUID roomId) {
        return reviewRepository.findAllByRoomId(roomId).stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public double getAverageRatingForRoom(UUID roomId) {
        List<Review> reviews = reviewRepository.findAllByRoomId(roomId);
        return reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
    }

    @Override
    public void flagOldLowRatedReviews() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(30);
        List<Review> lowRated = reviewRepository.findAllByRatingLessThanAndCreatedAtBefore(3, cutoff);
        lowRated.forEach(r -> r.setFlagged(true));
        reviewRepository.saveAll(lowRated);
        log.info("Flagged {} old low-rated reviews", lowRated.size());
    }

    @Override
    public void deleteFlaggedReviews() {
        List<Review> flagged = reviewRepository.findAllByFlaggedTrue();
        reviewRepository.deleteAll(flagged);
        log.info("Deleted {} flagged reviews", flagged.size());
    }

    private Review findEntityById(UUID id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found with id: " + id));
    }

    private ReviewResponseDto toDto(Review review) {
        return new ReviewResponseDto(
                review.getId(),
                review.getRoomId(),
                review.getGuestUsername(),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt(),
                review.isFlagged()
        );
    }
}
