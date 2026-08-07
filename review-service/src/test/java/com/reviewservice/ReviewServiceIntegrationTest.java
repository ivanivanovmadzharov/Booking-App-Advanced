package com.reviewservice;

import com.reviewservice.model.dto.ReviewRequestDto;
import com.reviewservice.model.entity.Review;
import com.reviewservice.repository.ReviewRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
class ReviewServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ReviewRepository reviewRepository;

    @AfterEach
    void cleanUp() {
        reviewRepository.deleteAll();
    }

    @Test
    void createAndFetchReview_fullFlow() throws Exception {
        UUID roomId = UUID.randomUUID();
        ReviewRequestDto request = new ReviewRequestDto();
        request.setRoomId(roomId);
        request.setGuestUsername("integrationuser");
        request.setRating(5);
        request.setComment("Fantastic stay, would visit again!");

        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.guestUsername").value("integrationuser"));

        mockMvc.perform(get("/api/reviews/room/" + roomId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].rating").value(5));

        assertThat(reviewRepository.findAllByRoomId(roomId)).hasSize(1);
    }
}
