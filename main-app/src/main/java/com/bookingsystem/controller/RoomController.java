package com.bookingsystem.controller;

import com.bookingsystem.config.UserPrincipal;
import com.bookingsystem.model.dto.BookingDto;
import com.bookingsystem.model.dto.ReviewDto;
import com.bookingsystem.model.dto.RoomDto;
import com.bookingsystem.model.entity.Room;
import com.bookingsystem.service.ReviewService;
import com.bookingsystem.service.RoomService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/rooms")
public class RoomController {

    private final RoomService roomService;
    private final ReviewService reviewService;

    public RoomController(RoomService roomService, ReviewService reviewService) {
        this.roomService = roomService;
        this.reviewService = reviewService;
    }

    @GetMapping
    public String listRooms(Model model) {
        model.addAttribute("rooms", roomService.findAllAvailable());
        return "rooms/list";
    }

    @GetMapping("/{id}")
    public String roomDetail(@PathVariable UUID id, Model model,
                              @AuthenticationPrincipal UserPrincipal principal) {
        Room room = roomService.findById(id);
        List<ReviewDto> reviews = reviewService.getReviewsForRoom(id);
        Double avgRating = reviewService.getAverageRating(id);

        model.addAttribute("room", room);
        model.addAttribute("reviews", reviews);
        model.addAttribute("avgRating", avgRating);
        model.addAttribute("bookingDto", new BookingDto());
        model.addAttribute("reviewDto", new ReviewDto());

        if (principal != null) {
            boolean hasReviewed = reviews.stream()
                    .anyMatch(r -> r.getGuestUsername().equals(principal.getUsername()));
            model.addAttribute("hasReviewed", hasReviewed);
            model.addAttribute("currentUsername", principal.getUsername());
        }
        return "rooms/detail";
    }

    @GetMapping("/new")
    public String newRoomForm(Model model) {
        model.addAttribute("roomDto", new RoomDto());
        return "rooms/form";
    }

    @PostMapping("/new")
    public String createRoom(@Valid @ModelAttribute("roomDto") RoomDto dto,
                              BindingResult bindingResult,
                              @AuthenticationPrincipal UserPrincipal principal,
                              Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", false);
            return "rooms/form";
        }
        Room room = roomService.createRoom(dto, principal.getUser());
        return "redirect:/rooms/" + room.getId();
    }

    @GetMapping("/{id}/edit")
    public String editRoomForm(@PathVariable UUID id, Model model) {
        Room room = roomService.findById(id);
        RoomDto dto = new RoomDto();
        dto.setTitle(room.getTitle());
        dto.setDescription(room.getDescription());
        dto.setLocation(room.getLocation());
        dto.setPricePerNight(room.getPricePerNight());
        dto.setMaxGuests(room.getMaxGuests());
        dto.setImageUrl(room.getImageUrl());
        dto.setAvailable(room.isAvailable());
        model.addAttribute("roomDto", dto);
        model.addAttribute("roomId", id);
        model.addAttribute("isEdit", true);
        return "rooms/form";
    }

    @PostMapping("/{id}/edit")
    public String updateRoom(@PathVariable UUID id,
                              @Valid @ModelAttribute("roomDto") RoomDto dto,
                              BindingResult bindingResult,
                              @AuthenticationPrincipal UserPrincipal principal,
                              Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", true);
            model.addAttribute("roomId", id);
            return "rooms/form";
        }
        roomService.updateRoom(id, dto, principal.getUser());
        return "redirect:/rooms/" + id;
    }

    @PostMapping("/{id}/delete")
    public String deleteRoom(@PathVariable UUID id,
                              @AuthenticationPrincipal UserPrincipal principal) {
        roomService.deleteRoom(id, principal.getUser());
        return "redirect:/rooms";
    }

    @GetMapping("/my")
    public String myRooms(@AuthenticationPrincipal UserPrincipal principal, Model model) {
        model.addAttribute("rooms", roomService.findAllByHost(principal.getUser().getId()));
        return "rooms/my-rooms";
    }
}
