package com.example.backend.services;

import com.example.backend.entities.Apartment;
import com.example.backend.entities.Profile;
import com.example.backend.entities.User;
import com.example.backend.exceptions.StateConflictException;
import com.example.backend.repositories.ProfileRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProfileService {
    private final ProfileRepository profileRepository;

    public ProfileService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    public Profile create(User user, String code) {
        if (user.getCurrentProfile() != null)
            throw new StateConflictException("You already have an apartment");


        return profileRepository.save(
                new Profile(

                )
        );
    }
}
