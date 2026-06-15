package com.example.backend.dtos.out.profile;

import com.example.backend.entities.Profile;
import com.example.backend.types.Color;

public record ProfileBriefDto(
        Long id,
        String name,
        Color avatarColor
) {
    public ProfileBriefDto(Profile profile) {
        this(
                profile.getId(),
                profile.getName(),
                profile.getAvatarColor()
        );
    }
}
