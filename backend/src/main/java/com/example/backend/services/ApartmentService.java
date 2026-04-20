package com.example.backend.services;

import com.example.backend.dtos.in.apartment.CreateApartmentDto;
import com.example.backend.dtos.out.profile.ProfileDto;
import com.example.backend.entities.Apartment;
import com.example.backend.entities.Profile;
import com.example.backend.entities.User;
import com.example.backend.exceptions.StateConflictException;
import com.example.backend.repositories.ApartmentRepository;
import com.example.backend.repositories.ProfileRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class ApartmentService {
    private final ApartmentRepository apartmentRepository;
    private final ProfileRepository profileRepository;

    public ApartmentService(
            ApartmentRepository apartmentRepository,
            ProfileRepository profileRepository
    ) {
        this.apartmentRepository = apartmentRepository;
        this. profileRepository = profileRepository;
    }

    @Transactional
    public ProfileDto create(User user, CreateApartmentDto dto) {
        if (user.getCurrentProfile() != null)
            throw new StateConflictException("You already have an apartment");

        Apartment apartment = apartmentRepository.save(new Apartment(
                dto.name(),
                dto.address()
        ));

        Profile profile = profileRepository.save(new Profile(user, apartment, true));

        return new ProfileDto(profile);
    }
}
