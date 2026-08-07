package com.bookingsystem.integration;

import com.bookingsystem.model.entity.User;
import com.bookingsystem.model.enums.UserRole;
import com.bookingsystem.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:integrationdb;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
        "review.service.url=http://localhost:8081",
        "spring.cloud.openfeign.lazy-attributes-resolution=true"
})
class MainAppIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @AfterEach
    void cleanUp() {
        userRepository.deleteAll();
    }

    @Test
    void homePage_isAccessibleByAnyone() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
    }

    @Test
    void roomsPage_isAccessibleByAnyone() throws Exception {
        mockMvc.perform(get("/rooms"))
                .andExpect(status().isOk());
    }

    @Test
    void profilePage_redirectsUnauthenticated() throws Exception {
        mockMvc.perform(get("/profile"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminDashboard_accessibleByAdmin() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "GUEST")
    void adminDashboard_forbiddenForGuest() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void register_withValidData_redirectsToLogin() throws Exception {
        mockMvc.perform(post("/register").with(csrf())
                        .param("username", "integrationuser")
                        .param("email", "integration@test.com")
                        .param("password", "password123")
                        .param("confirmPassword", "password123")
                        .param("role", "GUEST"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?registered=true"));
    }
}
