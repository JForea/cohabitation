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

    private UserDto getUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getCurrentProfile()
        );
    }

    private ProfileDto getProfileDto(Profile profile) {
        return new ProfileDto(
                profile.getId(),
                profile.getName(),
                profile.getPoints(),
                profile.getApartment().getId(),
                profile.getRole()
        );
    }

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

    public UserDto create(RegisterDto dto) {
        User user = userRepository.save(new User(
                dto.email(),
                passwordEncoder.encode(dto.password()),
                dto.name(),
                dto.male(),
                getRandomColor()
        ));

        return getUserDto(user);
    }

    public UserDto authenticate(AuthenticationDto dto) throws UsernameNotFoundException {
        User user = userRepository.findUserByEmail(dto.email()).orElseThrow(() ->
                new UsernameNotFoundException("User not found."));

        if (passwordEncoder.matches(dto.password(), user.getPassword()))
            return getUserDto(user);

        throw new UsernameNotFoundException("User not found.");
    }

    public ProfileDto join(User user, String code) throws StateConflictException {
        if (user.getCurrentProfile() != null)
            throw new StateConflictException("You already have an apartment");

        Apartment apartment = apartmentRepository.findByInviteCode(code).orElseThrow(() ->
                new ResourceNotFoundException("Apartment with such invite code wasn't found.")
        );

        Optional<Profile> oldProfile = profileRepository.findByApartmentAndUser(apartment, user);
        if (oldProfile.isPresent()) {
            Profile profile = oldProfile.get();
            profile.setLeftAt(null);
            profile.setName(user.getName());
            profileRepository.save(profile);
            return getProfileDto(profile);
        }

        Profile profile = profileRepository.save(
                new Profile(
                        user,
                        apartment
                )
        );

        return getProfileDto(profile);
    }
}
