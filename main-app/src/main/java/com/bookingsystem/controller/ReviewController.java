package com.bookingsystem.controller;

import com.bookingsystem.config.UserPrincipal;
import com.bookingsystem.model.dto.ReviewDto;
import com.bookingsystem.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/rooms/{roomId}")
    public String submitReview(@PathVariable UUID roomId,
                                @Valid @ModelAttribute("reviewDto") ReviewDto dto,
                                BindingResult bindingResult,
                                @AuthenticationPrincipal UserPrincipal principal) {
        if (bindingResult.hasErrors()) {
            return "redirect:/rooms/" + roomId + "?reviewError=true";
        }
        dto.setRoomId(roomId);
        dto.setGuestUsername(principal.getUsername());
        reviewService.submitReview(dto);
        return "redirect:/rooms/" + roomId + "?reviewed=true";
    }

    @PostMapping("/{id}/delete")
    public String deleteReview(@PathVariable UUID id,
                                @RequestParam UUID roomId) {
        reviewService.deleteReview(id);
        return "redirect:/rooms/" + roomId;
    }
}
