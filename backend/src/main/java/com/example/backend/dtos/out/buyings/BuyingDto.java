package com.example.backend.dtos.out.buyings;

import com.example.backend.dtos.out.profile.ProfileBriefDto;
import com.example.backend.dtos.out.profile.ProfileDto;
import com.example.backend.entities.Buying;
import com.example.backend.types.BuyingCategory;

public record BuyingDto(
        Long id,
        ProfileBriefDto createdBy,
        ProfileBriefDto assignedTo,
        ProfileBriefDto completedBy,
        String name,
        String quantity,
        BuyingCategory category
) {
    public BuyingDto(Buying buying) {
        this(
                buying.getId(),
                new ProfileBriefDto(buying.getCreatedBy()),
                buying.getAssignedTo() != null ? new ProfileBriefDto(buying.getAssignedTo()) : null,
                buying.getCompletedBy() != null ? new ProfileBriefDto(buying.getCompletedBy()) : null,
                buying.getName(),
                buying.getQuantity(),
                buying.getCategory()
        );
    }
}