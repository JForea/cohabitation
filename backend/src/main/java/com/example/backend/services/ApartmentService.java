package com.example.backend.services;

import com.example.backend.dtos.in.apartment.CreateApartmentDto;
import com.example.backend.dtos.out.profile.ProfileDto;
import com.example.backend.entities.Apartment;
import com.example.backend.entities.Profile;
import com.example.backend.entities.User;
import com.example.backend.exceptions.StateConflictException;
import com.example.backend.repositories.ApartmentRepository;
import com.example.backend.repositories.ProfileRepository;
import com.example.backend.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class ApartmentService {
    private final ApartmentRepository apartmentRepository;
    private final ProfileRepository profileRepository;

    private final UserRepository userRepository;

    public ApartmentService(
            ApartmentRepository apartmentRepository,
            ProfileRepository profileRepository,
            UserRepository userRepository) {
        this.apartmentRepository = apartmentRepository;
        this. profileRepository = profileRepository;
        this.userRepository = userRepository;
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

        user.setCurrentProfile(profile);
        userRepository.save(user);

        return new ProfileDto(profile);
    }
}
