package com.example.backend.services;

import com.example.backend.dtos.in.user.AuthenticationDto;
import com.example.backend.dtos.in.user.RegisterDto;
import com.example.backend.dtos.out.user.UserDto;
import com.example.backend.entities.ProfileMonthlyExpense;
import com.example.backend.entities.User;
import com.example.backend.repositories.ProfileMonthlyExpenseRepository;
import com.example.backend.repositories.UserRepository;
import com.example.backend.types.Color;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Month;
import java.util.Calendar;
import java.util.Optional;
import java.util.Random;

@Service
public class UserService {
    private final Random random;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final ProfileMonthlyExpenseRepository profileMonthlyExpenseRepository;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            ProfileMonthlyExpenseRepository profileMonthlyExpenseRepository) {
        this.random = new Random();
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.profileMonthlyExpenseRepository = profileMonthlyExpenseRepository;
    }

    private Color getRandomColor() {
        Color[] colors = Color.values();
        return colors[random.nextInt(colors.length)];
    }

    public UserDto create(RegisterDto dto) {
        User user = userRepository.save(new User(
                dto.email(),
                passwordEncoder.encode(dto.password()),
                dto.name(),
                dto.male(),
                getRandomColor()
        ));

        return new UserDto(user, 0);
    }

    public UserDto authenticate(AuthenticationDto dto) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(dto.email()).orElseThrow(() ->
                new UsernameNotFoundException("User not found."));

        if (!passwordEncoder.matches(dto.password(), user.getPassword()))
            throw new UsernameNotFoundException("User not found.");

        Month month = Month.of(Calendar.getInstance().get(Calendar.MONTH));

        Optional<ProfileMonthlyExpense> monthlyExpense = profileMonthlyExpenseRepository.
                findByProfileMonthlyExpenseKey_ProfileAndProfileMonthlyExpenseKey_Month(
                        user.getCurrentProfile(),
                        month
                );

        return new UserDto(user, monthlyExpense.isPresent() ? monthlyExpense.get().getAmount() : 0);
    }

    public UserDto getCurrentInfo(User user) {
        Month month = Month.of(Calendar.getInstance().get(Calendar.MONTH));

        Optional<ProfileMonthlyExpense> monthlyExpense = profileMonthlyExpenseRepository.
                findByProfileMonthlyExpenseKey_ProfileAndProfileMonthlyExpenseKey_Month(
                        user.getCurrentProfile(),
                        month
                );

        return new UserDto(user, monthlyExpense.isPresent() ? monthlyExpense.get().getAmount() : 0);
    }
}
