package com.example.backend.dtos.out.user;

import com.example.backend.entities.Profile;

public record UserDto(
    Integer id,
    String email,
    String name,
    Profile profile
) {}
