package com.example.backend.dtos.out.apartment;

import com.example.backend.dtos.out.profile.ProfileDto;
import com.example.backend.entities.Apartment;
import com.example.backend.entities.Profile;

public record JoinApartmentResponse(
        Integer id,
        String name,
        String address,
        Integer budget,
        Integer currentExpenseSum,
        ProfileDto profile
) {
    public JoinApartmentResponse(
            Apartment apartment,
            Integer currentExpenseSum,
            Profile profile,
            Integer profileExpenseAmount
    ) {
        this(
                apartment.getId(),
                apartment.getName(),
                apartment.getAddress(),
                apartment.getBudget(),
                currentExpenseSum,
                new ProfileDto(profile, profileExpenseAmount)
        );
    }
}
