package com.bookingsystem.controller;

import com.bookingsystem.config.UserPrincipal;
import com.bookingsystem.model.enums.UserRole;
import com.bookingsystem.service.BookingService;
import com.bookingsystem.service.RoomService;
import com.bookingsystem.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final BookingService bookingService;
    private final RoomService roomService;

    public AdminController(UserService userService,
                            BookingService bookingService,
                            RoomService roomService) {
        this.userService = userService;
        this.bookingService = bookingService;
        this.roomService = roomService;
    }

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("users", userService.findAll());
        model.addAttribute("totalRooms", roomService.findAll().size());
        model.addAttribute("totalBookings", bookingService.findAll().size());
        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String manageUsers(Model model) {
        model.addAttribute("users", userService.findAll());
        model.addAttribute("roles", UserRole.values());
        return "admin/users";
    }

    @PostMapping("/users/{id}/role")
    public String updateUserRole(@PathVariable UUID id,
                                  @RequestParam UserRole role) {
        userService.updateRole(id, role);
        return "redirect:/admin/users?updated=true";
    }

    @GetMapping("/bookings")
    public String allBookings(Model model) {
        model.addAttribute("bookings", bookingService.findAll());
        return "admin/bookings";
    }

    @PostMapping("/bookings/{id}/cancel")
    public String cancelBookingAdmin(@PathVariable UUID id,
                                      @AuthenticationPrincipal UserPrincipal principal) {
        bookingService.cancelBooking(id, principal.getUser());
        return "redirect:/admin/bookings";
    }
}
