package com.bookingsystem.service.impl;

import com.bookingsystem.client.ReviewClient;
import com.bookingsystem.model.dto.ReviewDto;
import com.bookingsystem.service.ReviewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ReviewServiceImpl implements ReviewService {

    private static final Logger log = LoggerFactory.getLogger(ReviewServiceImpl.class);

    private final ReviewClient reviewClient;

    public ReviewServiceImpl(ReviewClient reviewClient) {
        this.reviewClient = reviewClient;
    }

    @Override
    public List<ReviewDto> getReviewsForRoom(UUID roomId) {
        return reviewClient.getReviewsByRoom(roomId);
    }

    @Override
    public Double getAverageRating(UUID roomId) {
        return reviewClient.getAverageRating(roomId);
    }

    @Override
    public ReviewDto submitReview(ReviewDto dto) {
        log.info("Submitting review for room {} by {}", dto.getRoomId(), dto.getGuestUsername());
        return reviewClient.createReview(dto);
    }

    @Override
    public ReviewDto updateReview(UUID id, ReviewDto dto) {
        log.info("Updating review {}", id);
        return reviewClient.updateReview(id, dto);
    }

    @Override
    public void deleteReview(UUID id) {
        log.info("Deleting review {}", id);
        reviewClient.deleteReview(id);
    }
}
