package com.bookingsystem.controller;

import com.bookingsystem.config.UserPrincipal;
import com.bookingsystem.model.dto.ProfileDto;
import com.bookingsystem.model.entity.User;
import com.bookingsystem.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String profilePage(@AuthenticationPrincipal UserPrincipal principal, Model model) {
        User user = principal.getUser();
        ProfileDto dto = new ProfileDto();
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        model.addAttribute("profileDto", dto);
        model.addAttribute("user", user);
        return "user/profile";
    }

    @PostMapping
    public String updateProfile(@Valid @ModelAttribute("profileDto") ProfileDto dto,
                                 BindingResult bindingResult,
                                 @AuthenticationPrincipal UserPrincipal principal,
                                 Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", principal.getUser());
            return "user/profile";
        }
        userService.updateProfile(principal.getUser().getId(), dto);
        return "redirect:/profile?updated=true";
    }
}
