package com.reviewservice.service;

import com.reviewservice.exception.ReviewNotFoundException;
import com.reviewservice.model.dto.ReviewRequestDto;
import com.reviewservice.model.dto.ReviewResponseDto;
import com.reviewservice.model.entity.Review;
import com.reviewservice.repository.ReviewRepository;
import com.reviewservice.service.impl.ReviewServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private ReviewRequestDto requestDto;
    private Review savedReview;

    @BeforeEach
    void setUp() {
        requestDto = new ReviewRequestDto();
        requestDto.setRoomId(UUID.randomUUID());
        requestDto.setGuestUsername("testuser");
        requestDto.setRating(4);
        requestDto.setComment("Great room!");

        savedReview = new Review();
        savedReview.setId(UUID.randomUUID());
        savedReview.setRoomId(requestDto.getRoomId());
        savedReview.setGuestUsername("testuser");
        savedReview.setRating(4);
        savedReview.setComment("Great room!");
    }

    @Test
    void createReview_savesAndReturnsDto() {
        when(reviewRepository.save(any(Review.class))).thenReturn(savedReview);

        ReviewResponseDto result = reviewService.createReview(requestDto);

        assertThat(result.getGuestUsername()).isEqualTo("testuser");
        assertThat(result.getRating()).isEqualTo(4);
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void deleteReview_whenNotFound_throwsException() {
        UUID id = UUID.randomUUID();
        when(reviewRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.deleteReview(id))
                .isInstanceOf(ReviewNotFoundException.class)
                .hasMessageContaining("Review not found");
    }

    @Test
    void getAverageRating_whenNoReviews_returnsZero() {
        when(reviewRepository.findAllByRoomId(any())).thenReturn(java.util.List.of());

        double avg = reviewService.getAverageRatingForRoom(UUID.randomUUID());

        assertThat(avg).isEqualTo(0.0);
    }
}
