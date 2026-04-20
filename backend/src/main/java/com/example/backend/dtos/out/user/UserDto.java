package com.example.backend.dtos.out.user;

import com.example.backend.dtos.out.profile.ProfileDto;
import com.example.backend.entities.User;

public record UserDto(
    Integer id,
    String email,
    String name,
    ProfileDto profile
) {
    public UserDto(User user) {
        this(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getCurrentProfile() != null ? new ProfileDto(user.getCurrentProfile()) : null
        );
    }
}
