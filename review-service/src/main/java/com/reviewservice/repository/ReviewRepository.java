package com.reviewservice.repository;

import com.reviewservice.model.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {

    List<Review> findAllByRoomId(UUID roomId);

    List<Review> findAllByFlaggedTrue();

    List<Review> findAllByRatingLessThanAndCreatedAtBefore(int rating, LocalDateTime before);
}
