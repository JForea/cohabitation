package com.example.backend.services;

import com.example.backend.dtos.in.user.AuthenticationDto;
import com.example.backend.dtos.in.user.RegisterDto;
import com.example.backend.dtos.out.user.UserDto;
import com.example.backend.entities.User;
import com.example.backend.repositories.UserRepository;
import com.example.backend.types.Color;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class UserService {
    private final Random random;
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository repository,
            PasswordEncoder passwordEncoder
    ) {
        this.random = new Random();
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    private Color getRandomColor() {
        Color[] colors = Color.values();
        return colors[random.nextInt(colors.length)];
    }

    public UserDto create(RegisterDto dto) {
        User user = repository.save(new User(
                dto.email(),
                passwordEncoder.encode(dto.password()),
                dto.name(),
                dto.male(),
                getRandomColor()
        ));

        return new UserDto(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getCurrentProfile()
        );
    }

    public UserDto authenticate(AuthenticationDto dto) throws UsernameNotFoundException {
        User user = repository.findUserByEmail(dto.email()).orElseThrow(() ->
                new UsernameNotFoundException("User not found."));

        if (passwordEncoder.matches(dto.password(), user.getPassword()))
            return new UserDto(
                    user.getId(),
                    user.getEmail(),
                    user.getName(),
                    user.getCurrentProfile()
            );

        throw new UsernameNotFoundException("User not found.");
    }
}
