package com.reviewservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponseDto {

    private UUID id;
    private UUID roomId;
    private String guestUsername;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;
    private boolean flagged;
}
