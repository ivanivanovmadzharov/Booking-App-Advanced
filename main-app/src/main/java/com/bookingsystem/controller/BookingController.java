package com.bookingsystem.controller;

import com.bookingsystem.config.UserPrincipal;
import com.bookingsystem.model.dto.BookingDto;
import com.bookingsystem.service.BookingService;
import com.bookingsystem.service.RoomService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final RoomService roomService;

    public BookingController(BookingService bookingService, RoomService roomService) {
        this.bookingService = bookingService;
        this.roomService = roomService;
    }

    @PostMapping("/rooms/{roomId}")
    public String createBooking(@PathVariable UUID roomId,
                                 @Valid @ModelAttribute("bookingDto") BookingDto dto,
                                 BindingResult bindingResult,
                                 @AuthenticationPrincipal UserPrincipal principal,
                                 Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("room", roomService.findById(roomId));
            model.addAttribute("reviewDto", new com.bookingsystem.model.dto.ReviewDto());
            model.addAttribute("reviews", java.util.List.of());
            model.addAttribute("avgRating", 0.0);
            return "rooms/detail";
        }
        bookingService.createBooking(roomId, dto, principal.getUser());
        return "redirect:/bookings/my";
    }

    @GetMapping("/my")
    public String myBookings(@AuthenticationPrincipal UserPrincipal principal, Model model) {
        model.addAttribute("bookings", bookingService.findAllByGuest(principal.getUser().getId()));
        return "bookings/my-bookings";
    }

    @PostMapping("/{id}/cancel")
    public String cancelBooking(@PathVariable UUID id,
                                 @AuthenticationPrincipal UserPrincipal principal) {
        bookingService.cancelBooking(id, principal.getUser());
        return "redirect:/bookings/my";
    }
}
