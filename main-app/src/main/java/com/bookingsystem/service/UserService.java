package com.bookingsystem.service;

import com.bookingsystem.model.dto.ProfileDto;
import com.bookingsystem.model.dto.RegisterDto;
import com.bookingsystem.model.entity.User;
import com.bookingsystem.model.enums.UserRole;

import java.util.List;
import java.util.UUID;

public interface UserService {

    User register(RegisterDto dto);

    User findByUsername(String username);

    User findById(UUID id);

    User updateProfile(UUID id, ProfileDto dto);

    void updateRole(UUID id, UserRole role);

    List<User> findAll();

    boolean usernameExists(String username);

    boolean emailExists(String email);
}
