package com.example.backend.services;

import com.example.backend.dtos.out.profile.ProfileDto;
import com.example.backend.entities.Profile;
import com.example.backend.entities.User;
import com.example.backend.repositories.ProfileMonthlyExpenseRepository;
import com.example.backend.repositories.ProfileRepository;
import org.springframework.stereotype.Service;

import java.time.Month;
import java.time.Year;
import java.util.Calendar;
import java.util.List;

@Service
public class ProfileService {

    private final ProfileRepository profileRepository;

    private final ProfileMonthlyExpenseRepository profileMonthlyExpenseRepository;

    public ProfileService(ProfileRepository profileRepository,
                          ProfileMonthlyExpenseRepository profileMonthlyExpenseRepository) {
        this.profileRepository = profileRepository;
        this.profileMonthlyExpenseRepository = profileMonthlyExpenseRepository;
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
}
