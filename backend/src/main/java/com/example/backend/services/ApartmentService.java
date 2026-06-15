package com.example.backend.services;

import com.example.backend.dtos.in.apartment.CreateApartmentDto;
import com.example.backend.dtos.out.apartment.ApartmentDto;
import com.example.backend.dtos.out.apartment.CreateApartmentResponse;
import com.example.backend.dtos.out.apartment.InviteCodeResponse;
import com.example.backend.dtos.out.apartment.JoinApartmentResponse;
import com.example.backend.entities.*;
import com.example.backend.exceptions.ResourceNotFoundException;
import com.example.backend.exceptions.StateConflictException;
import com.example.backend.intefaces.ApartmentNotificationHandler;
import com.example.backend.repositories.*;
import com.example.backend.types.Role;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.Calendar;
import java.util.Optional;
import java.util.Random;

@Service
public class ApartmentService {

    private final ApartmentRepository apartmentRepository;

    private final ProfileRepository profileRepository;

    private final UserRepository userRepository;

    private final ApartmentNotificationHandler apartmentNotificationHandler;

    private final ExpenseRepository expenseRepository;

    private final Random random;

    public ApartmentService(
            ApartmentRepository apartmentRepository,
            ProfileRepository profileRepository,
            UserRepository userRepository,
            ApartmentNotificationHandler apartmentNotificationHandler,
            ExpenseRepository expenseRepository) {
        this.apartmentRepository = apartmentRepository;
        this. profileRepository = profileRepository;
        this.userRepository = userRepository;
        random = new Random();
        this.apartmentNotificationHandler = apartmentNotificationHandler;
        this.expenseRepository = expenseRepository;
    }

    private Integer getMonthlyExpensesInApartment(Apartment apartment) {
        YearMonth currentMonth = YearMonth.now();

        ZoneOffset offset = ZoneOffset.ofTotalSeconds(
                apartment.getMinutesOffset() * 60
        );

        Instant start = currentMonth
                .atDay(1)
                .atStartOfDay(offset)
                .toInstant();

        Instant end = currentMonth
                .plusMonths(1)
                .atDay(1)
                .atStartOfDay(offset)
                .toInstant();

        Integer amount = expenseRepository.getAmountSumByApartmentIdAndCreatedAtInPeriod(
                apartment.getId(),
                start,
                end
        );

        return amount != null ? amount : 0;
    }

    @Transactional
    public CreateApartmentResponse create(User user, CreateApartmentDto dto) {
        Apartment apartment = apartmentRepository.save(new Apartment(
                dto.name(),
                dto.address(),
                dto.minutesOffset()
        ));

        Profile profile = profileRepository.save(new Profile(user, apartment, true));

        user.setCurrentProfile(profile);
        userRepository.save(user);

        return new CreateApartmentResponse(
                apartment,
                profile
        );
    }

    public ApartmentDto get(User user, Integer apartmentId) {
        Role role = user.getCurrentProfile().getRole();

        Apartment apartment = apartmentRepository.findById(apartmentId).orElseThrow(() ->
                new ResourceNotFoundException("Apartment not found.")
        );

        Integer monthlyExpenses = getMonthlyExpensesInApartment(apartment);

        return new ApartmentDto(
                apartment,
                monthlyExpenses,
                role == Role.INHABITANT ? null : apartment.getInviteCode()
        );
    }

    private Integer getMonthlyExpensesByProfile(Profile profile) {
        Apartment apartment = profile.getApartment();

        ZoneOffset offset = ZoneOffset.ofTotalSeconds(
                apartment.getMinutesOffset() * 60
        );

        YearMonth currentMonth = YearMonth.now(offset);

        Instant start = currentMonth
                .atDay(1)
                .atStartOfDay(offset)
                .toInstant();

        Instant end = currentMonth
                .plusMonths(1)
                .atDay(1)
                .atStartOfDay(offset)
                .toInstant();

        Integer amount = expenseRepository.getAmountSumByProfileAndCreatedAtInPeriod(
                profile,
                start,
                end
        );

        return amount != null ? amount : 0;
    }

    @Transactional
    public JoinApartmentResponse join(User user, String code) throws StateConflictException {
        Apartment apartment = apartmentRepository.findByInviteCode(code).orElseThrow(() ->
                new ResourceNotFoundException("Apartment with such invite code wasn't found.")
        );

        Optional<Profile> oldProfile = profileRepository.findByApartmentAndUser(apartment, user);
        if (oldProfile.isPresent()) {
            Profile profile = oldProfile.get();

            profile.setLeftAt(null);
            profile.setName(user.getName());
            profile.setRole(Role.INHABITANT);
            profileRepository.save(profile);

            user.setCurrentProfile(profile);
            userRepository.save(user);

            Integer monthlyExpenses = getMonthlyExpensesInApartment(apartment);

            Integer profileMonthlyExpense = getMonthlyExpensesByProfile(profile);

            apartmentNotificationHandler.handleJoinNotification(profile, true);

            return new JoinApartmentResponse(
                    apartment,
                    monthlyExpenses,
                    profile,
                    profileMonthlyExpense != null ? profileMonthlyExpense : 0
            );
        }

        Profile profile = profileRepository.save(
                new Profile(
                        user,
                        apartment,
                        false
                )
        );

        user.setCurrentProfile(profile);
        userRepository.save(user);

        Integer monthlyExpenses = getMonthlyExpensesInApartment(apartment);

        apartmentNotificationHandler.handleJoinNotification(profile, false);

        return new JoinApartmentResponse(
                apartment,
                monthlyExpenses,
                profile,
                0
        );
    }

    @Transactional
    public InviteCodeResponse generateCode(Integer apartmentId) {
        int inviteCodeLength = 8;
        String charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

        boolean generated = false;
        StringBuilder inviteCodeBuilder = new StringBuilder();
        while (!generated) {
            inviteCodeBuilder = new StringBuilder();

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

        apartmentRepository.updateInviteCodeById(apartmentId, inviteCode);

        return new InviteCodeResponse(inviteCode);
    }

    @Transactional
    public void setBudget(Integer apartmentId, Integer budget) {
        apartmentRepository.updateBudgetById(apartmentId, budget);
    }

    @Transactional
    public void deleteApartment(Integer apartmentId) {
        apartmentRepository.deleteById(apartmentId);
    }

    @Transactional
    public void leave(User user) {
        Profile profile = user.getCurrentProfile();
        profileRepository.updateLeftAtById(profile.getId(), Instant.now());
        userRepository.setCurrentProfileNullWhereId(user.getId());

        apartmentNotificationHandler.handleLeaveNotification(profile);
    }
}
