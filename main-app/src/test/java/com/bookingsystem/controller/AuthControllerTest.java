package com.bookingsystem.controller;

import com.bookingsystem.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private com.bookingsystem.config.CustomUserDetailsService customUserDetailsService;

    @Test
    @WithAnonymousUser
    void loginPage_returnsLoginView() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"));
    }

    @Test
    @WithAnonymousUser
    void registerPage_returnsRegisterView() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"));
    }

    @Test
    @WithAnonymousUser
    void register_withMismatchedPasswords_returnsRegisterView() throws Exception {
        mockMvc.perform(post("/register").with(csrf())
                        .param("username", "newuser")
                        .param("email", "new@test.com")
                        .param("password", "password123")
                        .param("confirmPassword", "differentpassword")
                        .param("role", "GUEST"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"));
    }

    @Test
    @WithAnonymousUser
    void register_withInvalidEmail_returnsRegisterView() throws Exception {
        mockMvc.perform(post("/register").with(csrf())
                        .param("username", "newuser")
                        .param("email", "not-an-email")
                        .param("password", "password123")
                        .param("confirmPassword", "password123")
                        .param("role", "GUEST"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"));
    }
}
