package com.example.backend.dtos.out.profile;

import com.example.backend.types.Role;

public record ProfileDto(
    Long id,
    String name,
    Integer points,
    Integer apartmentId,
    Role role
) {}
