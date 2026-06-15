package com.example.backend.dtos.out.profile;

import com.example.backend.entities.Profile;
import com.example.backend.types.Color;
import com.example.backend.types.Role;

public record ProfileDto(
    Long id,
    String name,
    Integer points,
    Integer apartmentId,
    Role role,
    Color avatarColor,
    Integer monthlyExpensesAmount
) {
    public ProfileDto(Profile profile, Integer monthlyExpensesAmount) {
        this(
                profile.getId(),
                profile.getName(),
                profile.getPoints(),
                profile.getApartment().getId(),
                profile.getRole(),
                profile.getAvatarColor(),
                monthlyExpensesAmount
        );
    }
}
