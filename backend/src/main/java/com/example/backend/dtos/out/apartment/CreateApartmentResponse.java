package com.example.backend.dtos.out.apartment;

import com.example.backend.dtos.out.profile.ProfileDto;

public record CreateApartmentResponse(
        Integer id,
        Integer budget,
        ProfileDto profile
) {}
