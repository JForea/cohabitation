package com.example.backend.services;

import com.example.backend.dtos.in.apartment.CreateApartmentDto;
import com.example.backend.dtos.out.apartment.ApartmentDto;
import com.example.backend.dtos.out.apartment.CreateApartmentResponse;
import com.example.backend.dtos.out.apartment.InviteCodeResponse;
import com.example.backend.dtos.out.apartment.JoinApartmentResponse;
import com.example.backend.entities.Apartment;
import com.example.backend.entities.Profile;
import com.example.backend.entities.User;
import com.example.backend.exceptions.ResourceNotFoundException;
import com.example.backend.intefaces.ApartmentNotificationHandler;
import com.example.backend.repositories.ApartmentRepository;
import com.example.backend.repositories.ProfileMonthlyExpenseRepository;
import com.example.backend.repositories.ProfileRepository;
import com.example.backend.repositories.UserRepository;
import com.example.backend.types.Color;
import com.example.backend.types.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.Month;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ApartmentServiceTest {

    @Mock
    private ApartmentRepository apartmentRepository;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProfileMonthlyExpenseRepository profileMonthlyExpenseRepository;

    @Mock
    private ApartmentNotificationHandler apartmentNotificationHandler;

    @Mock
    private Random random;

    @InjectMocks
    private ApartmentService apartmentService;

    @Test
    void shouldCreateApartment() {
        User user = new User();
        Apartment savedApartment = new Apartment();
        Profile savedProfile = new Profile();

        CreateApartmentDto dto = new CreateApartmentDto(
                "Квартира",
                "ул. Ленина, 42",
                (short) 0
        );

        Integer apartmentId = 1;
        Instant now = Instant.now();

        ReflectionTestUtils.setField(savedApartment, "id", apartmentId);
        ReflectionTestUtils.setField(savedApartment, "createdAt", now);

        savedProfile.setApartment(savedApartment);

        when(apartmentRepository.save(any(Apartment.class))).thenReturn(savedApartment);

        when(profileRepository.save(any(Profile.class))).thenReturn(savedProfile);

        when(userRepository.save(any(User.class))).thenReturn(user);

        CreateApartmentResponse result = apartmentService.create(user, dto);

        assertNotNull(result);
        assertEquals(apartmentId, result.profile().apartmentId());

        verify(apartmentRepository).save(any(Apartment.class));
        verify(profileRepository).save(any(Profile.class));
        verify(userRepository).save(user);

        assertEquals(savedProfile, user.getCurrentProfile());
    }

    @Test
    void shouldThrowOnGetIfNoApartment() {
        User user = mock(User.class);
        Profile profile = mock(Profile.class);

        when(user.getCurrentProfile()).thenReturn(profile);
        when(profile.getRole()).thenReturn(Role.INHABITANT);

        when(apartmentRepository.findById(any(Integer.class))).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> apartmentService.get(user, 1));
    }

    @Test
    void shouldGetInviteCodeOnApartmentGetIfRoleAdmin() {
        User user = mock(User.class);
        Profile profile = mock(Profile.class);
        Apartment apartment = mock(Apartment.class);

        Integer apartmentId = 1;

        when(apartment.getId()).thenReturn(apartmentId);
        when(apartment.getCreatedAt()).thenReturn(Instant.now());

        when(user.getCurrentProfile()).thenReturn(profile);
        when(profile.getRole()).thenReturn(Role.ADMIN);

        String inviteCode = "inviteCode";

        when(apartmentRepository.findById(apartmentId)).thenReturn(Optional.of(apartment));

        when(apartment.getInviteCode()).thenReturn(inviteCode);

        ApartmentDto result = apartmentService.get(user, apartmentId);

        assertNotNull(result);
        assertEquals(inviteCode, result.inviteCode());
        assertEquals(apartmentId, result.id());
        assertEquals(0, result.currentExpenseSum());
    }

    @Test
    void shouldGetInviteCodeOnApartmentGetIfRoleCreator() {
        User user = mock(User.class);
        Profile profile = mock(Profile.class);
        Apartment apartment = mock(Apartment.class);

        Integer apartmentId = 1;

        when(apartment.getId()).thenReturn(apartmentId);
        when(apartment.getCreatedAt()).thenReturn(Instant.now());

        when(user.getCurrentProfile()).thenReturn(profile);
        when(profile.getRole()).thenReturn(Role.CREATOR);

        String inviteCode = "inviteCode";

        when(apartmentRepository.findById(apartmentId)).thenReturn(Optional.of(apartment));

        when(apartment.getInviteCode()).thenReturn(inviteCode);

        ApartmentDto result = apartmentService.get(user, apartmentId);

        assertNotNull(result);
        assertEquals(inviteCode, result.inviteCode());
        assertEquals(apartmentId, result.id());
        assertEquals(0, result.currentExpenseSum());
    }

    @Test
    void shouldNotGetInviteCodeOnApartmentGetIfRoleInhabitant() {
        User user = mock(User.class);
        Profile profile = mock(Profile.class);
        Apartment apartment = mock(Apartment.class);

        Integer apartmentId = 1;

        when(apartment.getId()).thenReturn(apartmentId);
        when(apartment.getCreatedAt()).thenReturn(Instant.now());

        when(user.getCurrentProfile()).thenReturn(profile);
        when(profile.getRole()).thenReturn(Role.INHABITANT);

        when(apartmentRepository.findById(apartmentId)).thenReturn(Optional.of(apartment));

        ApartmentDto result = apartmentService.get(user, apartmentId);

        assertNotNull(result);
        assertNull(result.inviteCode());
        assertEquals(apartmentId, result.id());
        assertEquals(0, result.currentExpenseSum());
    }

    @Test
    void shouldThrowOnJoinIfInviteCodeNotFound() {
        User user = mock(User.class);
        String inviteCode = "HOME0001";

        when(apartmentRepository.findByInviteCode(inviteCode)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> apartmentService.join(user, inviteCode));
    }

    @Test
    void shouldTakeOldProfileIfExists() {
        String userName = "Марк";

        User user = new User(
                "test@gmail.com",
                "password",
                userName,
                true,
                Color.BLUE
        );
        String inviteCode = "HOME0001";
        Long profileId = 1L;

        Apartment apartment = new Apartment();
        Profile oldProfile = new Profile();

        ReflectionTestUtils.setField(oldProfile, "apartment", apartment);
        ReflectionTestUtils.setField(oldProfile, "id", profileId);
        ReflectionTestUtils.setField(apartment, "id", 1);
        ReflectionTestUtils.setField(apartment, "createdAt", Instant.now());

        when(apartmentRepository.findByInviteCode(inviteCode)).thenReturn(Optional.of(apartment));

        when(profileRepository.findByApartmentAndUser(apartment, user)).thenReturn(Optional.of(oldProfile));

        Integer monthlyExpenses = 0;

        when(profileMonthlyExpenseRepository.getSumByProfileAndYearAndMonth(
                any(Profile.class),
                anyInt(),
                any(Month.class)
        )).thenReturn(monthlyExpenses);


        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        ArgumentCaptor<Profile> profileCaptor = ArgumentCaptor.forClass(Profile.class);

        JoinApartmentResponse result = apartmentService.join(user, inviteCode);

        verify(userRepository).save(userCaptor.capture());
        verify(profileRepository).save(profileCaptor.capture());

        User userSaved = userCaptor.getValue();
        Profile profileSaved = profileCaptor.getValue();

        assertNotNull(result);

        assertEquals(oldProfile, userSaved.getCurrentProfile());
        assertEquals(profileId, profileSaved.getId());
        assertNull(profileSaved.getLeftAt());
        assertEquals(Role.INHABITANT, profileSaved.getRole());

        verify(apartmentNotificationHandler).handleJoinNotification(profileSaved, true);
    }

    @Test
    void shouldMakeNewProfileIfNotExists() {
        String userName = "Марк";

        User user = new User(
                "test@gmail.com",
                "password",
                userName,
                true,
                Color.BLUE
        );

        String inviteCode = "HOME0001";

        Apartment apartment = new Apartment();

        ReflectionTestUtils.setField(apartment, "id", 1);
        ReflectionTestUtils.setField(
                apartment,
                "createdAt",
                Instant.now()
        );

        when(apartmentRepository.findByInviteCode(inviteCode))
                .thenReturn(Optional.of(apartment));

        when(profileRepository.findByApartmentAndUser(
                apartment,
                user
        )).thenReturn(Optional.empty());

        when(profileRepository.save(any(Profile.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        ArgumentCaptor<Profile> profileCaptor = ArgumentCaptor.forClass(Profile.class);

        JoinApartmentResponse result = apartmentService.join(user, inviteCode);

        verify(userRepository).save(userCaptor.capture());

        verify(profileRepository).save(profileCaptor.capture());

        User savedUser = userCaptor.getValue();
        Profile savedProfile = profileCaptor.getValue();

        assertNotNull(result);

        assertEquals(savedProfile, savedUser.getCurrentProfile());

        assertEquals(user, savedProfile.getUser());
        assertEquals(apartment, savedProfile.getApartment());

        verify(apartmentNotificationHandler).handleJoinNotification(savedProfile, false);
    }

    @Test
    void shouldGenerateInviteCode() {
        Integer apartmentId = 1;

        when(apartmentRepository.findByInviteCode(anyString())).thenReturn(Optional.empty());

        InviteCodeResponse result = apartmentService.generateCode(apartmentId);

        assertNotNull(result);

        String inviteCode = result.inviteCode();

        assertEquals(8, inviteCode.length());

        verify(apartmentRepository).findByInviteCode(inviteCode);

        verify(apartmentRepository).updateInviteCodeById(
                apartmentId,
                inviteCode
        );
    }

    @Test
    void shouldRegenerateInviteCodeIfExists() {
        Integer apartmentId = 1;

        Apartment conflictingApartment = mock(Apartment.class);

        when(apartmentRepository.findByInviteCode(anyString())).thenReturn(
                Optional.of(conflictingApartment),
                Optional.empty()
        );

        InviteCodeResponse result =
                apartmentService.generateCode(apartmentId);

        assertNotNull(result);

        verify(apartmentRepository, times(2))
                .findByInviteCode(anyString());

        verify(apartmentRepository)
                .updateInviteCodeById(
                        eq(apartmentId),
                        anyString()
                );

        assertEquals(8, result.inviteCode().length());
    }

    @Test
    void shouldSetBudget() {
        Integer apartmentId = 1;
        Integer budget = 50000;

        apartmentService.setBudget(apartmentId, budget);

        verify(apartmentRepository).updateBudgetById(
                apartmentId,
                budget
        );
    }

    @Test
    void shouldDeleteApartment() {
        Integer apartmentId = 1;

        apartmentService.deleteApartment(apartmentId);

        verify(apartmentRepository).deleteById(apartmentId);
    }

    @Test
    void shouldLeaveApartment() {
        User user = mock(User.class);
        Profile profile = mock(Profile.class);

        Integer userId = 1;
        Long profileId = 2L;

        when(user.getId()).thenReturn(userId);
        when(user.getCurrentProfile()).thenReturn(profile);
        when(profile.getId()).thenReturn(profileId);

        apartmentService.leave(user);

        verify(profileRepository).updateLeftAtById(eq(profileId), any(Instant.class));

        verify(userRepository).setCurrentProfileNullWhereId(userId);

        verify(apartmentNotificationHandler).handleLeaveNotification(profile);
    }

}
