package com.example.backend.services;

import com.example.backend.dtos.in.user.AuthenticationDto;
import com.example.backend.dtos.in.user.RegisterDto;
import com.example.backend.dtos.out.user.UserDto;
import com.example.backend.entities.Apartment;
import com.example.backend.entities.Profile;
import com.example.backend.entities.User;
import com.example.backend.exceptions.ResourceNotFoundException;
import com.example.backend.exceptions.StateConflictException;
import com.example.backend.repositories.ApartmentRepository;
import com.example.backend.repositories.ExpenseRepository;
import com.example.backend.repositories.UserRepository;
import com.example.backend.types.Color;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.Random;

@Service
public class UserService {
    private final Random random;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final ExpenseRepository expenseRepository;

    private final ApartmentRepository apartmentRepository;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            ExpenseRepository expenseRepository,
            ApartmentRepository apartmentRepository) {
        this.random = new Random();
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.expenseRepository = expenseRepository;
        this.apartmentRepository = apartmentRepository;
    }

    private Color getRandomColor() {
        Color[] colors = Color.values();
        return colors[random.nextInt(colors.length)];
    }

    public UserDto create(RegisterDto dto) {
        if (userRepository.existsByEmail(dto.email()))
            throw new StateConflictException("Email is occupied.");

        User user = userRepository.save(new User(
                dto.email(),
                passwordEncoder.encode(dto.password()),
                dto.name(),
                dto.male(),
                getRandomColor()
        ));

        return new UserDto(user, 0);
    }

    private Integer getMonthlyExpensesByProfile(Profile profile) {
        if (profile == null) {
            return 0;
        }

        Apartment apartment = apartmentRepository.findById(profile.getApartment().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Apartment not found."));

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

    public UserDto authenticate(AuthenticationDto dto) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(dto.email()).orElseThrow(() ->
                new UsernameNotFoundException("User not found."));

        if (!passwordEncoder.matches(dto.password(), user.getPassword())) {
            throw new UsernameNotFoundException("User not found.");
        }

        Integer monthlyExpense = getMonthlyExpensesByProfile(user.getCurrentProfile());

        return new UserDto(user, monthlyExpense);
    }

    public UserDto getCurrentInfo(User user) {
        Integer monthlyExpense = getMonthlyExpensesByProfile(user.getCurrentProfile());

        return new UserDto(user, monthlyExpense);
    }
}
