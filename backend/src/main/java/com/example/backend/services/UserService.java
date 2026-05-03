package com.example.backend.services;

import com.example.backend.dtos.in.user.AuthenticationDto;
import com.example.backend.dtos.in.user.RegisterDto;
import com.example.backend.dtos.out.profile.ProfileDto;
import com.example.backend.dtos.out.user.UserDto;
import com.example.backend.entities.Apartment;
import com.example.backend.entities.Profile;
import com.example.backend.entities.User;
import com.example.backend.exceptions.ResourceNotFoundException;
import com.example.backend.exceptions.StateConflictException;
import com.example.backend.repositories.ApartmentRepository;
import com.example.backend.repositories.ProfileRepository;
import com.example.backend.repositories.UserRepository;
import com.example.backend.types.Color;
import com.example.backend.types.Role;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
public class UserService {
    private final Random random;
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final ApartmentRepository apartmentRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            ProfileRepository profileRepository,
            ApartmentRepository apartmentRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.random = new Random();
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.apartmentRepository = apartmentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private Color getRandomColor() {
        Color[] colors = Color.values();
        return colors[random.nextInt(colors.length)];
    }

    public Role getCurrentUserRoleInApartment(User user, Integer apartmentId) {
        Optional<Profile> profileOptional = profileRepository.findByUserAndApartment_id(user, apartmentId);

        if (profileOptional.isEmpty())
            return null;

        Profile profile = profileOptional.get();

        if (profile.getLeftAt() != null)
            return null;

        return profile.getRole();
    }

    public UserDto create(RegisterDto dto) {
        User user = userRepository.save(new User(
                dto.email(),
                passwordEncoder.encode(dto.password()),
                dto.name(),
                dto.male(),
                getRandomColor()
        ));

        return new UserDto(user);
    }

    public UserDto authenticate(AuthenticationDto dto) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(dto.email()).orElseThrow(() ->
                new UsernameNotFoundException("User not found."));

        if (passwordEncoder.matches(dto.password(), user.getPassword()))
            return new UserDto(user);

        throw new UsernameNotFoundException("User not found.");
    }
}
