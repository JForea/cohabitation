package com.example.backend.services;

import com.example.backend.dtos.in.apartment.CreateApartmentDto;
import com.example.backend.dtos.out.apartment.ApartmentDto;
import com.example.backend.dtos.out.apartment.CreateApartmentResponse;
import com.example.backend.dtos.out.apartment.InviteCodeResponse;
import com.example.backend.dtos.out.profile.ProfileDto;
import com.example.backend.entities.Apartment;
import com.example.backend.entities.MonthlyExpense;
import com.example.backend.entities.Profile;
import com.example.backend.entities.User;
import com.example.backend.entities.keys.MonthlyExpenseKey;
import com.example.backend.exceptions.AccessForbiddenException;
import com.example.backend.exceptions.ResourceNotFoundException;
import com.example.backend.exceptions.StateConflictException;
import com.example.backend.repositories.ApartmentRepository;
import com.example.backend.repositories.MonthlyExpenseRepository;
import com.example.backend.repositories.ProfileRepository;
import com.example.backend.repositories.UserRepository;
import com.example.backend.types.Role;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Month;
import java.util.Calendar;
import java.util.Optional;
import java.util.Random;

@Service
public class ApartmentService {

    private final ApartmentRepository apartmentRepository;

    private final ProfileRepository profileRepository;

    private final UserRepository userRepository;

    private final UserService userService;

    private final MonthlyExpenseRepository monthlyExpenseRepository;

    private final Random random;

    public ApartmentService(
            ApartmentRepository apartmentRepository,
            ProfileRepository profileRepository,
            UserRepository userRepository,
            UserService userService,
            MonthlyExpenseRepository monthlyExpenseRepository) {
        this.apartmentRepository = apartmentRepository;
        this. profileRepository = profileRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.monthlyExpenseRepository = monthlyExpenseRepository;
        random = new Random();
    }

    @Transactional
    public CreateApartmentResponse create(User user, CreateApartmentDto dto) {
        if (user.getCurrentProfile() != null)
            throw new StateConflictException("You already have an apartment");

        Apartment apartment = apartmentRepository.save(new Apartment(
                dto.name(),
                dto.address()
        ));

        Profile profile = profileRepository.save(new Profile(user, apartment, true));

        user.setCurrentProfile(profile);
        userRepository.save(user);

        return new CreateApartmentResponse(
                apartment.getId(),
                apartment.getBudget(),
                new ProfileDto(profile)
        );
    }

    public ApartmentDto get(User user, Integer apartmentId) {
        Role role = userService.getCurrentUserRoleInApartment(user, apartmentId);

        if (role == null)
            throw new AccessForbiddenException("You can't get information about this apartment.");

        Apartment apartment = apartmentRepository.findById(apartmentId).orElseThrow(() ->
                new ResourceNotFoundException("Apartment not found.")
        );

        Month month = Month.of(Calendar.getInstance().get(Calendar.MONTH));

        Optional<MonthlyExpense> monthlyExpense = monthlyExpenseRepository.findById(
                new MonthlyExpenseKey(
                        apartment,
                        month
                )
        );

        return new ApartmentDto(
                apartment,
                monthlyExpense.isPresent() ? monthlyExpense.get().getSum() : 0
        );
    }

    public InviteCodeResponse generateCode(User user, Integer apartmentId) {
        Role role = userService.getCurrentUserRoleInApartment(user, apartmentId);

        if (role == null || role == Role.INHABITANT)
            throw new AccessForbiddenException("You can't generate invite code in this apartment.");

        int inviteCodeLength = 8;
        String charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

        Apartment apartment = apartmentRepository.findById(apartmentId).orElseThrow(
                () -> new ResourceNotFoundException("Apartment not found.")
        );

        boolean generated = false;
        StringBuilder inviteCodeBuilder = new StringBuilder();
        while (!generated) {
            for (int i = 0; i < inviteCodeLength; i++) {
                inviteCodeBuilder.append(charset.charAt(random.nextInt(charset.length())));
            }

            Optional<Apartment> conflictingApartment = apartmentRepository.findByInviteCode(inviteCodeBuilder.toString());

            if (conflictingApartment.isEmpty())
                generated = true;
            else
                inviteCodeBuilder.delete(0, inviteCodeLength);
        }

        String inviteCode = inviteCodeBuilder.toString();
        apartment.setInviteCode(inviteCode);

        apartmentRepository.save(apartment);

        return new InviteCodeResponse(inviteCode);
    }
}
