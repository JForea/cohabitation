package com.example.backend.dtos.out.buyings;

import com.example.backend.dtos.out.profile.ProfileDto;
import com.example.backend.entities.Buying;
import com.example.backend.types.BuyingCategory;

public record BuyingDto(
        Long id,
        ProfileDto createdBy,
        ProfileDto assignedTo,
        ProfileDto completedBy,
        String name,
        String quantity,
        BuyingCategory category
) {
    public BuyingDto(Buying buying) {
        this(
                buying.getId(),
                new ProfileDto(buying.getCreatedBy()),
                buying.getAssignedTo() != null ? new ProfileDto(buying.getAssignedTo()) : null,
                buying.getCompletedBy() != null ? new ProfileDto(buying.getCompletedBy()) : null,
                buying.getName(),
                buying.getQuantity(),
                buying.getCategory()
        );
    }
}