package com.example.backend.services;

import com.example.backend.dtos.out.profile.ProfileDto;
import com.example.backend.entities.Apartment;
import com.example.backend.entities.Profile;
import com.example.backend.entities.User;
import com.example.backend.exceptions.AccessForbiddenException;
import com.example.backend.exceptions.BadRequestException;
import com.example.backend.exceptions.ResourceNotFoundException;
import com.example.backend.intefaces.ProfileNotificationHandler;
import com.example.backend.repositories.ProfileMonthlyExpenseRepository;
import com.example.backend.repositories.ProfileRepository;
import com.example.backend.repositories.UserRepository;
import com.example.backend.types.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProfileServiceTest {

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private ProfileMonthlyExpenseRepository profileMonthlyExpenseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProfileNotificationHandler profileNotificationHandler;

    @InjectMocks
    private ProfileService profileService;

    @Test
    void shouldReturnProfilesIncludingUser() {
        User user = mock(User.class);
        Profile profile1 = mock(Profile.class);
        Profile profile2 = mock(Profile.class);
        Apartment apartment = new Apartment();

        when(profile1.getApartment()).thenReturn(apartment);
        when(profile2.getApartment()).thenReturn(apartment);

        Integer apartmentId = 1;

        when(profileRepository.findAllByApartment_IdAndLeftAtNull(apartmentId))
                .thenReturn(List.of(profile1, profile2));

        when(profileMonthlyExpenseRepository
                .getSumByProfileAndYearAndMonth(any(), anyInt(), any()))
                .thenReturn(100);

        List<ProfileDto> result = profileService.getAll(user, apartmentId, false);

        verify(profileRepository)
                .findAllByApartment_IdAndLeftAtNull(apartmentId);

        assertEquals(2, result.size());
    }

    @Test
    void shouldKickUserSuccessfully() {
        User user = mock(User.class);

        Profile userProfile = mock(Profile.class);
        Profile targetProfile = mock(Profile.class);

        User targetUser = mock(User.class);

        when(user.getCurrentProfile()).thenReturn(userProfile);

        Long profile1Id = 1L;
        Long profile2Id = 2L;

        Integer userId = 3;

        when(userProfile.getId()).thenReturn(profile1Id);

        when(targetProfile.getUser()).thenReturn(targetUser);
        when(targetUser.getId()).thenReturn(userId);

        when(profileRepository.findById(profile2Id)).thenReturn(Optional.of(targetProfile));

        profileService.kick(user, profile2Id);

        verify(userRepository).setCurrentProfileNullWhereId(userId);

        verify(profileRepository).updateLeftAtById(eq(profile2Id), any(Instant.class));

        verify(profileNotificationHandler).handleKickNotification(userProfile, targetProfile);
    }

    @Test
    void shouldThrowWhenUserTriesToKickSelf() {
        User user = mock(User.class);
        Profile userProfile = mock(Profile.class);

        when(user.getCurrentProfile()).thenReturn(userProfile);
        when(userProfile.getId()).thenReturn(1L);

        assertThrows(BadRequestException.class, () ->
                profileService.kick(user, 1L)
        );

        verifyNoInteractions(profileRepository);
        verifyNoInteractions(userRepository);
    }

    @Test
    void shouldThrowOnKickWhenTargetNotFound() {
        User user = mock(User.class);
        Profile userProfile = mock(Profile.class);

        when(user.getCurrentProfile()).thenReturn(userProfile);
        when(userProfile.getId()).thenReturn(1L);

        when(profileRepository.findById(2L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                profileService.kick(user, 2L)
        );

        verify(profileRepository).findById(2L);

        verifyNoInteractions(userRepository);
        verifyNoInteractions(profileNotificationHandler);
    }

    @Test
    void shouldPreventAdminKickingNonInhabitant() {
        User user = mock(User.class);

        Profile adminProfile = mock(Profile.class);
        Profile targetProfile = mock(Profile.class);

        when(user.getCurrentProfile()).thenReturn(adminProfile);

        when(adminProfile.getId()).thenReturn(1L);
        when(adminProfile.getRole()).thenReturn(Role.ADMIN);

        when(targetProfile.getRole()).thenReturn(Role.CREATOR);

        when(profileRepository.findById(2L))
                .thenReturn(Optional.of(targetProfile));

        assertThrows(AccessForbiddenException.class, () ->
                profileService.kick(user, 2L)
        );

        verifyNoInteractions(userRepository);
        verifyNoInteractions(profileNotificationHandler);
    }

    @Test
    void shouldThrowOnSetRoleWhenTargetProfileNotFound() {
        User user = mock(User.class);

        when(profileRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                profileService.setRole(user, 1L, Role.ADMIN)
        );

        verifyNoInteractions(profileNotificationHandler);
        verify(profileRepository, never()).save(any());
    }

    @Test
    void shouldSetRoleNotCreator() {
        User user = mock(User.class);

        Profile userProfile = mock(Profile.class);
        Profile target = mock(Profile.class);

        when(user.getCurrentProfile()).thenReturn(userProfile);

        when(profileRepository.findById(2L))
                .thenReturn(Optional.of(target));

        Role result = profileService.setRole(user, 2L, Role.INHABITANT);

        verify(target).setRole(Role.INHABITANT);

        verify(profileRepository).save(target);

        verify(profileNotificationHandler)
                .handleRoleChange(target, Role.INHABITANT);

        assertNull(result);
    }

    @Test
    void shouldSetRoleCreator() {
        User user = mock(User.class);

        Profile userProfile = mock(Profile.class);
        Profile target = mock(Profile.class);

        when(user.getCurrentProfile()).thenReturn(userProfile);

        when(profileRepository.findById(2L))
                .thenReturn(Optional.of(target));

        Role result = profileService.setRole(user, 2L, Role.CREATOR);

        verify(target).setRole(Role.CREATOR);
        verify(userProfile).setRole(Role.ADMIN);

        verify(profileRepository).saveAll(anyList());

        verify(profileNotificationHandler)
                .handleRoleChange(target, Role.CREATOR);

        assertEquals(Role.ADMIN, result);
    }

}
