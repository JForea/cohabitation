package com.example.backend.services;

import com.example.backend.dtos.out.profile.ProfileDto;
import com.example.backend.entities.Profile;
import com.example.backend.entities.User;
import com.example.backend.exceptions.AccessForbiddenException;
import com.example.backend.repositories.ProfileRepository;
import com.example.backend.types.Role;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProfileService {

    private final UserService userService;

    private final ProfileRepository profileRepository;

    public ProfileService(UserService userService,
                          ProfileRepository profileRepository) {
        this.userService = userService;
        this.profileRepository = profileRepository;
    }

    public List<ProfileDto> getAll(User user, Integer apartmentId, Boolean excludeMe) {
        Role role = userService.getCurrentUserRoleInApartment(user, apartmentId);

        if (role == null)
            throw new AccessForbiddenException("You can't get profiles in this apartment.");

        List<Profile> profiles;
        if (excludeMe == null || !excludeMe)
            profiles = profileRepository.findAllByApartment_Id(apartmentId);
        else
            profiles = profileRepository.findAllByApartment_IdAndUserNot(apartmentId, user);

        return profiles.stream().map(ProfileDto::new).toList();
    }
}
