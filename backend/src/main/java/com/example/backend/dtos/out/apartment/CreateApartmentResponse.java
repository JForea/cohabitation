package com.example.backend.dtos.out.apartment;

import com.example.backend.dtos.out.profile.ProfileDto;
import com.example.backend.entities.Apartment;
import com.example.backend.entities.Profile;

import java.time.LocalDate;
import java.time.ZoneId;

public record CreateApartmentResponse(
        Integer id,
        LocalDate createdAt,
        Integer budget,
        ProfileDto profile
) {
    public CreateApartmentResponse(Apartment apartment, Profile profile) {
        this(
                apartment.getId(),
                apartment.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDate(),
                apartment.getBudget(),
                new ProfileDto(profile, 0)
        );
    }
}
