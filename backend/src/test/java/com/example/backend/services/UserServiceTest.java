package com.example.backend.services;

import com.example.backend.dtos.in.user.AuthenticationDto;
import com.example.backend.dtos.in.user.RegisterDto;
import com.example.backend.dtos.out.user.UserDto;
import com.example.backend.entities.Apartment;
import com.example.backend.entities.Profile;
import com.example.backend.entities.User;
import com.example.backend.exceptions.StateConflictException;
import com.example.backend.repositories.ProfileMonthlyExpenseRepository;
import com.example.backend.repositories.UserRepository;
import com.example.backend.types.Color;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Month;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ProfileMonthlyExpenseRepository profileMonthlyExpenseRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldCreateUser() {
        RegisterDto dto = new RegisterDto(
                "test@gmail.com",
                "password",
                "Марк",
                true,
                null
        );

        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        UserDto result = userService.create(dto);

        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();

        assertEquals("test@gmail.com", savedUser.getEmail());
        assertEquals("encodedPassword", savedUser.getPassword());
        assertEquals("Марк", savedUser.getName());

        assertEquals("test@gmail.com", result.email());
        assertEquals("Марк", result.name());
        assertNull(result.profile());
    }

    @Test
    void shouldThrowOnCreateWhenEmailAlreadyExists() {
        RegisterDto dto = new RegisterDto(
                "test@mail.com",
                "password",
                "John",
                true,
                null
        );

        when(userRepository.existsByEmail("test@mail.com")).thenReturn(true);

        assertThrows(StateConflictException.class, () -> userService.create(dto));

        verify(userRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void shouldAuthenticateUserWithoutProfile() {
        AuthenticationDto dto = new AuthenticationDto(
                "test@mail.com",
                "password",
                null
        );

        User user = mock(User.class);

        when(user.getPassword()).thenReturn("encodedPassword");
        when(user.getCurrentProfile()).thenReturn(null);

        when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.of(user));

        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);

        UserDto result = userService.authenticate(dto);

        assertNotNull(result);
    }

    @Test
    void shouldAuthenticateUserWithProfile() {
        AuthenticationDto dto = new AuthenticationDto(
                "test@gmail.com",
                "password",
                null
        );

        User user = mock(User.class);
        Profile profile = mock(Profile.class);
        Apartment apartment = mock(Apartment.class);

        when(user.getPassword()).thenReturn("encodedPassword");
        when(user.getCurrentProfile()).thenReturn(profile);
        when(profile.getApartment()).thenReturn(apartment);
        when(apartment.getId()).thenReturn(1);

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("password", "encodedPassword"))
                .thenReturn(true);

        when(profileMonthlyExpenseRepository.getSumByProfileAndYearAndMonth(
                eq(profile),
                anyInt(),
                any(Month.class)
        )).thenReturn(1000);

        UserDto result = userService.authenticate(dto);

        assertNotNull(result);
        assertEquals(1000, result.profile().monthlyExpensesAmount());

        verify(userRepository).findByEmail("test@gmail.com");

        verify(passwordEncoder).matches("password", "encodedPassword");

        verify(profileMonthlyExpenseRepository).getSumByProfileAndYearAndMonth(
                eq(profile),
                anyInt(),
                any(Month.class)
        );
    }

    @Test
    void shouldThrowOnAuthenticateWhenUserNotFound() {
        AuthenticationDto dto = new AuthenticationDto(
                "test@gmail.com",
                "password",
                null
        );

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                UsernameNotFoundException.class,
                () -> userService.authenticate(dto)
        );

        verify(userRepository).findByEmail("test@gmail.com");

        verify(passwordEncoder, never()).matches(any(), any());

        verify(profileMonthlyExpenseRepository, never()).getSumByProfileAndYearAndMonth(any(), anyInt(), any());
    }

    @Test
    void shouldThrowOnAuthenticateWhenPasswordNotMatches() {
        AuthenticationDto dto = new AuthenticationDto(
                "test@gmail.com",
                "wrongPassword",
                null
        );

        User user = new User(
                "test@gmail.com",
                "encodedPassword",
                "Марк",
                true,
                Color.BLUE
        );

        when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "wrongPassword",
                "encodedPassword"
        )).thenReturn(false);

        assertThrows(
                UsernameNotFoundException.class,
                () -> userService.authenticate(dto)
        );

        verify(userRepository).findByEmail("test@gmail.com");

        verify(passwordEncoder).matches("wrongPassword", "encodedPassword");

        verify(profileMonthlyExpenseRepository, never()).getSumByProfileAndYearAndMonth(any(), anyInt(), any());
    }

    @Test
    void shouldPutZeroInMonthlyExpensesIfNullOnAuthenticate() {
        AuthenticationDto dto = new AuthenticationDto(
                "test@gmail.com",
                "password",
                null
        );

        Apartment apartment = mock(Apartment.class);

        Profile profile = new Profile();
        profile.setApartment(apartment);

        User user = new User(
                "test@gmail.com",
                "encodedPassword",
                "Марк",
                true,
                Color.BLUE
        );
        user.setCurrentProfile(profile);

        when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "password",
                "encodedPassword"
        )).thenReturn(true);

        when(apartment.getId()).thenReturn(1);

        when(profileMonthlyExpenseRepository
                .getSumByProfileAndYearAndMonth(
                        any(),
                        anyInt(),
                        any(Month.class)
                )).thenReturn(null);

        UserDto result = userService.authenticate(dto);

        assertNotNull(result);

        assertEquals(0, result.profile().monthlyExpensesAmount());

        verify(profileMonthlyExpenseRepository).getSumByProfileAndYearAndMonth(
                eq(profile),
                anyInt(),
                any(Month.class)
        );
    }

    @Test
    void shouldPutZeroInMonthlyExpensesIfNullOnGetCurrentInfo() {
        User user = new User(
                "test@gmail.com",
                "encodedPassword",
                "Марк",
                true,
                Color.BLUE
        );

        Profile profile = new Profile();

        Apartment apartment = mock(Apartment.class);

        profile.setApartment(apartment);

        user.setCurrentProfile(profile);

        when(apartment.getId()).thenReturn(1);

        when(profileMonthlyExpenseRepository
                .getSumByProfileAndYearAndMonth(
                        any(),
                        anyInt(),
                        any(Month.class)
                )).thenReturn(null);

        UserDto result = userService.getCurrentInfo(user);

        assertNotNull(result);

        assertEquals(0, result.profile().monthlyExpensesAmount());

        verify(profileMonthlyExpenseRepository).getSumByProfileAndYearAndMonth(
                eq(profile),
                anyInt(),
                any(Month.class)
        );
    }
}
