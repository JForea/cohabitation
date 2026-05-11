package com.example.backend.services;

import com.example.backend.dtos.out.profile.ProfileDto;
import com.example.backend.entities.Profile;
import com.example.backend.entities.User;
import com.example.backend.exceptions.AccessForbiddenException;
import com.example.backend.exceptions.BadRequestException;
import com.example.backend.exceptions.ResourceNotFoundException;
import com.example.backend.intefaces.ProfileNotificationHandler;
import com.example.backend.repositories.ProfileMonthlyExpenseRepository;
import com.example.backend.repositories.ProfileRepository;
import com.example.backend.repositories.UserRepository;
import com.example.backend.types.Role;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.Month;
import java.time.Year;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

@Service
public class ProfileService {

    private final ProfileRepository profileRepository;

    private final ProfileMonthlyExpenseRepository profileMonthlyExpenseRepository;

    private final UserRepository userRepository;

    private final ProfileNotificationHandler profileNotificationHandler;

    public ProfileService(ProfileRepository profileRepository,
                          ProfileMonthlyExpenseRepository profileMonthlyExpenseRepository,
                          UserRepository userRepository,
                          ProfileNotificationHandler profileNotificationHandler) {
        this.profileRepository = profileRepository;
        this.profileMonthlyExpenseRepository = profileMonthlyExpenseRepository;
        this.userRepository = userRepository;
        this.profileNotificationHandler = profileNotificationHandler;
    }

    public List<ProfileDto> getAll(User user, Integer apartmentId, Boolean excludeMe) {
        List<Profile> profiles;
        if (excludeMe == null || !excludeMe)
            profiles = profileRepository.findAllByApartment_Id(apartmentId);
        else
            profiles = profileRepository.findAllByApartment_IdAndUserNot(apartmentId, user);

        Calendar calendar = Calendar.getInstance();

        Year year = Year.of(calendar.get(Calendar.YEAR));
        Month month = Month.of(calendar.get(Calendar.MONTH));

        Integer monthlyExpense = profileMonthlyExpenseRepository.
                getSumByProfileAndYearAndMonth(
                        user.getCurrentProfile(),
                        year.getValue(),
                        month
                );

        return profiles.stream().map(p -> new ProfileDto(
                p, monthlyExpense != null ? monthlyExpense : 0
        )).toList();
    }

    @Transactional
    public void kick(User user, Long profileId) {
        Profile userProfile = user.getCurrentProfile();
        if (Objects.equals(profileId, userProfile.getId()))
            throw new BadRequestException("You can't kick yourself.");

        Profile target = profileRepository.findById(profileId).orElseThrow(
                () -> new ResourceNotFoundException("Target profile not found.")
        );

        if (userProfile.getRole() == Role.ADMIN && target.getRole() != Role.INHABITANT)
            throw new AccessForbiddenException("You can't kick this user.");

        userRepository.setCurrentProfileNullWhereId(target.getUser().getId());
        profileRepository.updateLeftAtById(profileId, Instant.now());

        profileNotificationHandler.handleKickNotification(userProfile, target);
    }

    @Transactional
    public Role setRole(User user, Long profileId, Role role) {
        Profile target = profileRepository.findById(profileId).orElseThrow(
                () -> new ResourceNotFoundException("Target profile not found.")
        );
        Profile userProfile = user.getCurrentProfile();

        target.setRole(role);
        if (role == Role.CREATOR) {
            userProfile.setRole(Role.ADMIN);
            profileRepository.saveAll(List.of(userProfile, target));
            profileNotificationHandler.handleRoleChange(target, role);
            return Role.ADMIN;
        }

        profileRepository.save(target);
        profileNotificationHandler.handleRoleChange(target, role);
        return null;
    }
}
