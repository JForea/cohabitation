package com.example.backend.services;

import com.example.backend.dtos.out.profile.ProfileDto;
import com.example.backend.entities.Profile;
import com.example.backend.entities.ProfileMonthlyExpense;
import com.example.backend.entities.User;
import com.example.backend.repositories.ProfileMonthlyExpenseRepository;
import com.example.backend.repositories.ProfileRepository;
import org.springframework.stereotype.Service;

import java.time.Month;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

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

        Month month = Month.of(Calendar.getInstance().get(Calendar.MONTH));

        Optional<ProfileMonthlyExpense> monthlyExpense = profileMonthlyExpenseRepository.
                findByProfileMonthlyExpenseKey_ProfileAndProfileMonthlyExpenseKey_Month(
                        user.getCurrentProfile(),
                        month
                );

        return profiles.stream().map(p -> new ProfileDto(
                p, monthlyExpense.isPresent() ? monthlyExpense.get().getAmount() : 0
        )).toList();
    }
}
