package com.bookingsystem.service;

import com.bookingsystem.exception.ResourceNotFoundException;
import com.bookingsystem.model.dto.ProfileDto;
import com.bookingsystem.model.dto.RegisterDto;
import com.bookingsystem.model.entity.User;
import com.bookingsystem.model.enums.UserRole;
import com.bookingsystem.repository.UserRepository;
import com.bookingsystem.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @InjectMocks private UserServiceImpl userService;

    private User existingUser;

    @BeforeEach
    void setUp() {
        existingUser = new User();
        existingUser.setId(UUID.randomUUID());
        existingUser.setUsername("testuser");
        existingUser.setEmail("test@test.com");
        existingUser.setPassword("encoded");
        existingUser.setRole(UserRole.GUEST);
    }

    @Test
    void register_encodesPasswordAndSaves() {
        RegisterDto dto = new RegisterDto();
        dto.setUsername("newuser");
        dto.setEmail("new@test.com");
        dto.setPassword("plaintext");
        dto.setRole(UserRole.GUEST);

        when(passwordEncoder.encode("plaintext")).thenReturn("encoded");
        when(userRepository.save(any())).thenReturn(existingUser);

        User result = userService.register(dto);

        verify(passwordEncoder).encode("plaintext");
        verify(userRepository).save(any(User.class));
        assertThat(result).isNotNull();
    }

    @Test
    void findByUsername_whenNotFound_throwsException() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findByUsername("ghost"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateProfile_updatesFieldsAndSaves() {
        ProfileDto dto = new ProfileDto();
        dto.setFullName("John Doe");
        dto.setEmail("john@test.com");
        dto.setPhone("0888123456");

        when(userRepository.findById(existingUser.getId())).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any())).thenReturn(existingUser);

        userService.updateProfile(existingUser.getId(), dto);

        assertThat(existingUser.getFullName()).isEqualTo("John Doe");
        assertThat(existingUser.getPhone()).isEqualTo("0888123456");
        verify(userRepository).save(existingUser);
    }

    @Test
    void updateRole_changesRoleAndSaves() {
        when(userRepository.findById(existingUser.getId())).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any())).thenReturn(existingUser);

        userService.updateRole(existingUser.getId(), UserRole.HOST);

        assertThat(existingUser.getRole()).isEqualTo(UserRole.HOST);
        verify(userRepository).save(existingUser);
    }

    @Test
    void usernameExists_delegatesToRepository() {
        when(userRepository.existsByUsername("testuser")).thenReturn(true);
        assertThat(userService.usernameExists("testuser")).isTrue();
    }
}
