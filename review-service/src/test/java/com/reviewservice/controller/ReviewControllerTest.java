package com.reviewservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reviewservice.model.dto.ReviewRequestDto;
import com.reviewservice.model.dto.ReviewResponseDto;
import com.reviewservice.service.ReviewService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReviewController.class)
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReviewService reviewService;

    @Test
    void getByRoom_returnsOk() throws Exception {
        UUID roomId = UUID.randomUUID();
        ReviewResponseDto dto = new ReviewResponseDto(UUID.randomUUID(), roomId,
                "user1", 5, "Excellent!", LocalDateTime.now(), false);

        when(reviewService.getReviewsByRoom(roomId)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/reviews/room/" + roomId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].guestUsername").value("user1"))
                .andExpect(jsonPath("$[0].rating").value(5));
    }

    @Test
    void createReview_withValidBody_returnsCreated() throws Exception {
        UUID roomId = UUID.randomUUID();
        ReviewRequestDto request = new ReviewRequestDto();
        request.setRoomId(roomId);
        request.setGuestUsername("user1");
        request.setRating(4);
        request.setComment("Very nice place!");

        ReviewResponseDto response = new ReviewResponseDto(UUID.randomUUID(), roomId,
                "user1", 4, "Very nice place!", LocalDateTime.now(), false);

        when(reviewService.createReview(any())).thenReturn(response);

        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rating").value(4));
    }

    @Test
    void createReview_withInvalidRating_returnsBadRequest() throws Exception {
        ReviewRequestDto request = new ReviewRequestDto();
        request.setRoomId(UUID.randomUUID());
        request.setGuestUsername("user1");
        request.setRating(10);
        request.setComment("Too high rating");

        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteReview_returnsNoContent() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/api/reviews/" + id))
                .andExpect(status().isNoContent());
    }
}
