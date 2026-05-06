package com.example.backend.dtos.out.profile;

import com.example.backend.entities.Profile;
import com.example.backend.types.Color;
import com.example.backend.types.Role;

public record NeighbourDto(
        Long id,
        String name,
        Integer points,
        Role role,
        Color avatarColor,
        Integer monthlyExpensesAmount
) {
    NeighbourDto(Profile profile, Integer monthlyExpensesAmount) {
        this(
                profile.getId(),
                profile.getName(),
                profile.getPoints(),
                profile.getRole(),
                profile.getAvatarColor(),
                monthlyExpensesAmount
        );
    }
}
