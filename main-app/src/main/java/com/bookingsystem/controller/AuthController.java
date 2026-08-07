package com.bookingsystem.controller;

import com.bookingsystem.model.dto.RegisterDto;
import com.bookingsystem.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("registerDto", new RegisterDto());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registerDto") RegisterDto dto,
                            BindingResult bindingResult) {
        if (dto.getUsername() != null && userService.usernameExists(dto.getUsername())) {
            bindingResult.rejectValue("username", "duplicate", "This username is already taken");
        }
        if (dto.getEmail() != null && userService.emailExists(dto.getEmail())) {
            bindingResult.rejectValue("email", "duplicate", "This email is already registered");
        }
        if (dto.getPassword() != null && !dto.getPassword().equals(dto.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "mismatch", "Passwords do not match");
        }
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }
        userService.register(dto);
        return "redirect:/login?registered=true";
    }
}
