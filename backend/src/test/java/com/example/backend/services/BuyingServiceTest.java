package com.example.backend.services;

import com.example.backend.dtos.in.buyings.CreateBuyingDto;
import com.example.backend.dtos.in.buyings.CreateBuyingShortDto;
import com.example.backend.dtos.in.buyings.CreateManyBuyingsDto;
import com.example.backend.dtos.out.buyings.BuyingDto;
import com.example.backend.dtos.out.common.IdResponse;
import com.example.backend.dtos.out.common.StatusResponse;
import com.example.backend.entities.Buying;
import com.example.backend.entities.Profile;
import com.example.backend.entities.User;
import com.example.backend.exceptions.AccessForbiddenException;
import com.example.backend.exceptions.ResourceNotFoundException;
import com.example.backend.intefaces.BuyingNotificationHandler;
import com.example.backend.repositories.BuyingRepository;
import com.example.backend.repositories.ProfileRepository;
import com.example.backend.types.BuyingCategory;
import com.example.backend.types.Color;
import com.example.backend.types.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BuyingServiceTest {

    @Mock
    private BuyingRepository buyingRepository;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private BuyingNotificationHandler buyingNotificationHandler;

    @InjectMocks
    private BuyingService buyingService;

    @Test
    void shouldCreateBuying() {
        User user = mock(User.class);

        Profile createdBy = new Profile();
        Profile assignedTo = new Profile();

        when(user.getCurrentProfile()).thenReturn(createdBy);

        CreateBuyingDto dto = new CreateBuyingDto(
                null,
                "Хлеб",
                "1 шт",
                BuyingCategory.BAKERY,
                true
        );

        when(buyingRepository.save(any(Buying.class))).thenAnswer(invocation -> {
            Buying buying = invocation.getArgument(0);
            ReflectionTestUtils.setField(buying, "id", 1L);
            return buying;
        });

        IdResponse<Long> response = buyingService.create(user, dto);

        assertEquals(1L, response.id());

        verify(buyingRepository).save(any(Buying.class));
        verify(buyingNotificationHandler).handleBuyingCreate(createdBy, null);
    }

    @Test
    void shouldThrowOnCreateIfAssignedUserNotFound() {
        User user = mock(User.class);

        Profile createdBy = new Profile();
        when(user.getCurrentProfile()).thenReturn(createdBy);

        CreateBuyingDto dto = new CreateBuyingDto(
                10L,
                "Хлеб",
                "1 шт",
                BuyingCategory.BAKERY,
                true
        );

        when(profileRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> buyingService.create(user, dto));

        verify(profileRepository).findById(10L);
        verify(buyingRepository, never()).save(any());
        verify(buyingNotificationHandler, never()).handleBuyingCreate(any(), any());
    }

    @Test
    void shouldCreateManyBuyings() {
        User user = mock(User.class);

        Profile createdBy = new Profile();
        when(user.getCurrentProfile()).thenReturn(createdBy);

        CreateBuyingShortDto b1 = new CreateBuyingShortDto(
                "Молоко",
                "2 л",
                BuyingCategory.DAIRY
        );

        CreateBuyingShortDto b2 = new CreateBuyingShortDto(
                "Хлеб",
                "1 шт",
                BuyingCategory.BAKERY
        );

        CreateManyBuyingsDto dto = new CreateManyBuyingsDto(
                List.of(b1, b2),
                null,
                true
        );

        when(buyingRepository.saveAll(anyList())).thenAnswer(invocation -> {
            List<Buying> input = invocation.getArgument(0);

            for (int i = 0; i < input.size(); i++) {
                ReflectionTestUtils.setField(input.get(i), "id", (long) (i + 1));
            }

            return input;
        });

        List<IdResponse<Long>> result = buyingService.createMany(user, dto);

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).id());
        assertEquals(2L, result.get(1).id());

        verify(buyingRepository).saveAll(anyList());
        verify(buyingNotificationHandler).handleBuyingCreate(createdBy, null);
    }

    @Test
    void shouldThrowOnCreateManyIfAssignedToProfileInvalid() {
        User user = mock(User.class);

        Profile createdBy = new Profile();
        when(user.getCurrentProfile()).thenReturn(createdBy);

        CreateBuyingShortDto b1 = new CreateBuyingShortDto(
                "Хлеб",
                "1 шт",
                BuyingCategory.BAKERY
        );

        Long assignedToId = 1L;

        CreateManyBuyingsDto dto = new CreateManyBuyingsDto(
                List.of(b1),
                assignedToId,
                true
        );

        when(profileRepository.findById(assignedToId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> buyingService.createMany(user, dto));

        verify(profileRepository).findById(assignedToId);
        verify(buyingRepository, never()).saveAll(any());
        verify(buyingNotificationHandler, never())
                .handleBuyingCreate(any(), any());
    }

    @Test
    void shouldGetBuyingList() {
        User user = mock(User.class);
        when(user.getId()).thenReturn(1);

        Integer apartmentId = 1;
        Integer assignedTo = null;
        Boolean isPublic = true;

        Profile createdBy = mock(Profile.class);
        when(createdBy.getId()).thenReturn(10L);
        when(createdBy.getName()).thenReturn("Alice");
        when(createdBy.getAvatarColor()).thenReturn(Color.BLUE);

        Buying buying = mock(Buying.class);

        when(buying.getId()).thenReturn(1L);
        when(buying.getCreatedBy()).thenReturn(createdBy);
        when(buying.getAssignedTo()).thenReturn(null);
        when(buying.getCompletedBy()).thenReturn(null);
        when(buying.getName()).thenReturn("Хлеб");
        when(buying.getQuantity()).thenReturn("1 шт");
        when(buying.getCategory()).thenReturn(BuyingCategory.BAKERY);

        when(buyingRepository.findAll(any(Specification.class), any(Sort.class)))
                .thenReturn(List.of(buying));

        List<BuyingDto> result = buyingService.get(apartmentId, user, assignedTo, isPublic);

        assertEquals(1, result.size());
        assertEquals("Хлеб", result.get(0).name());

        verify(buyingRepository).findAll(any(Specification.class), eq(Sort.by("category")));
    }

    @Test
    void shouldChangeStatusToDone() {
        User user = mock(User.class);

        Profile profile = mock(Profile.class);
        when(profile.getRole()).thenReturn(Role.INHABITANT);
        when(user.getCurrentProfile()).thenReturn(profile);

        Profile creator = mock(Profile.class);

        Buying buying = new Buying();
        ReflectionTestUtils.setField(buying, "createdBy", creator);
        buying.setIsPublic(true);

        Integer apartmentId = 1;
        Long buyingId = 10L;

        when(buyingRepository.findByCreatedBy_Apartment_IdAndId(apartmentId, buyingId))
                .thenReturn(Optional.of(buying));

        when(buyingRepository.save(any(Buying.class))).thenAnswer(
                invocation -> invocation.getArgument(0)
        );

        StatusResponse response = buyingService.changeStatus(apartmentId, user, buyingId);

        assertTrue(buying.getCompletedAt() != null);
        assertEquals(profile, buying.getCompletedBy());
        assertTrue(response.status());

        verify(buyingRepository).save(buying);
    }

    @Test
    void shouldChangeStatusToUndone() {
        User user = mock(User.class);

        Long profileId = 1L;

        Profile profile = mock(Profile.class);
        when(profile.getId()).thenReturn(profileId);
        when(profile.getRole()).thenReturn(Role.INHABITANT);
        when(user.getCurrentProfile()).thenReturn(profile);

        Profile creator = mock(Profile.class);

        Profile completedBy = mock(Profile.class);
        when(completedBy.getId()).thenReturn(profileId);

        Buying buying = new Buying();
        ReflectionTestUtils.setField(buying, "createdBy", creator);
        ReflectionTestUtils.setField(buying, "completedBy", completedBy);
        buying.setCompletedAt(Instant.now());
        buying.setIsPublic(true);

        when(buyingRepository.findByCreatedBy_Apartment_IdAndId(1, 10L))
                .thenReturn(Optional.of(buying));

        when(buyingRepository.save(any(Buying.class))).thenAnswer(inv -> inv.getArgument(0));

        StatusResponse response = buyingService.changeStatus(1, user, 10L);

        assertNull(buying.getCompletedAt());
        assertNull(buying.getCompletedBy());
        assertFalse(response.status());

        verify(buyingRepository).save(buying);
    }

    @Test
    void shouldThrowOnChangeStatusIfNotFound() {
        Integer apartmentId = 1;
        Long buyingId = 10L;

        when(buyingRepository.findByCreatedBy_Apartment_IdAndId(apartmentId, buyingId))
                .thenReturn(Optional.empty());

        User user = mock(User.class);

        assertThrows(ResourceNotFoundException.class,
                () -> buyingService.changeStatus(apartmentId, user, buyingId));

        verify(buyingRepository).findByCreatedBy_Apartment_IdAndId(apartmentId, buyingId);
        verify(buyingRepository, never()).save(any());
    }

    @Test
    void shouldThrowOnChangeStatusIfPublicAndDoneByOtherUserAndUserIsInhabitant() {
        Integer apartmentId = 1;
        Long buyingId = 10L;

        User user = mock(User.class);

        Profile currentProfile = mock(Profile.class);
        when(currentProfile.getId()).thenReturn(1L);
        when(currentProfile.getRole()).thenReturn(Role.INHABITANT);

        when(user.getCurrentProfile()).thenReturn(currentProfile);

        Profile createdBy = mock(Profile.class);

        Profile completedBy = mock(Profile.class);
        when(completedBy.getId()).thenReturn(2L);

        Buying buying = new Buying();
        ReflectionTestUtils.setField(buying, "createdBy", createdBy);
        ReflectionTestUtils.setField(buying, "completedBy", completedBy);
        buying.setIsPublic(true);
        buying.setCompletedAt(Instant.now());

        when(buyingRepository.findByCreatedBy_Apartment_IdAndId(apartmentId, buyingId))
                .thenReturn(Optional.of(buying));

        assertThrows(AccessForbiddenException.class,
                () -> buyingService.changeStatus(apartmentId, user, buyingId));

        verify(buyingRepository).findByCreatedBy_Apartment_IdAndId(apartmentId, buyingId);
        verify(buyingRepository, never()).save(any());
    }

    @Test
    void shouldThrowOnChangeStatusIfIsNotPublicAndUserIsNotBuyingCreator() {
        Integer apartmentId = 1;
        Long buyingId = 10L;

        User user = mock(User.class);

        Profile currentProfile = mock(Profile.class);
        when(currentProfile.getId()).thenReturn(1L);
        when(user.getCurrentProfile()).thenReturn(currentProfile);

        Profile creator = mock(Profile.class);
        when(creator.getId()).thenReturn(999L);

        Buying buying = new Buying();
        ReflectionTestUtils.setField(buying, "createdBy", creator);
        buying.setIsPublic(false);
        buying.setCompletedAt(null);

        when(buyingRepository.findByCreatedBy_Apartment_IdAndId(apartmentId, buyingId))
                .thenReturn(Optional.of(buying));

        assertThrows(AccessForbiddenException.class,
                () -> buyingService.changeStatus(apartmentId, user, buyingId));

        verify(buyingRepository).findByCreatedBy_Apartment_IdAndId(apartmentId, buyingId);
        verify(buyingRepository, never()).save(any());
    }

    @Test
    void shouldPassOnChangeStatusIfPublicAndDoneByOtherUserAndUserIsAdmin() {
        Integer apartmentId = 1;
        Long buyingId = 10L;

        User user = mock(User.class);

        Profile currentProfile = mock(Profile.class);
        when(currentProfile.getRole()).thenReturn(Role.ADMIN);

        when(user.getCurrentProfile()).thenReturn(currentProfile);

        Profile createdBy = mock(Profile.class);

        Profile completedBy = mock(Profile.class);

        Buying buying = new Buying();
        ReflectionTestUtils.setField(buying, "createdBy", createdBy);
        ReflectionTestUtils.setField(buying, "completedBy", completedBy);
        buying.setIsPublic(true);
        buying.setCompletedAt(Instant.now());

        when(buyingRepository.findByCreatedBy_Apartment_IdAndId(apartmentId, buyingId))
                .thenReturn(Optional.of(buying));

        when(buyingRepository.save(any(Buying.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        StatusResponse response = buyingService.changeStatus(apartmentId, user, buyingId);

        verify(buyingRepository).save(buying);

        assertNull(buying.getCompletedAt());
        assertNull(buying.getCompletedBy());
        assertFalse(response.status());
    }

    @Test
    void shouldPassOnChangeStatusIfPublicAndDoneByOtherUserAndUserIsCreator() {
        Integer apartmentId = 1;
        Long buyingId = 10L;

        User user = mock(User.class);

        Profile currentProfile = mock(Profile.class);
        when(currentProfile.getRole()).thenReturn(Role.CREATOR);

        when(user.getCurrentProfile()).thenReturn(currentProfile);

        Profile createdBy = mock(Profile.class);

        Profile completedBy = mock(Profile.class);

        Buying buying = new Buying();
        ReflectionTestUtils.setField(buying, "createdBy", createdBy);
        ReflectionTestUtils.setField(buying, "completedBy", completedBy);
        buying.setIsPublic(true);
        buying.setCompletedAt(Instant.now());

        when(buyingRepository.findByCreatedBy_Apartment_IdAndId(apartmentId, buyingId))
                .thenReturn(Optional.of(buying));

        when(buyingRepository.save(any(Buying.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        StatusResponse response = buyingService.changeStatus(apartmentId, user, buyingId);

        verify(buyingRepository).save(buying);

        assertNull(buying.getCompletedAt());
        assertNull(buying.getCompletedBy());
        assertFalse(response.status());
    }

    @Test
    void shouldDeleteOneIfUserIsBuyingCreator() {
        Integer apartmentId = 1;
        Long buyingId = 10L;

        User user = mock(User.class);

        Profile profile = mock(Profile.class);
        when(profile.getId()).thenReturn(1L);
        when(profile.getRole()).thenReturn(Role.INHABITANT);
        when(user.getCurrentProfile()).thenReturn(profile);

        Profile createdBy = mock(Profile.class);
        when(createdBy.getId()).thenReturn(1L);

        Buying buying = new Buying();
        ReflectionTestUtils.setField(buying, "createdBy", createdBy);

        when(buyingRepository.findByCreatedBy_Apartment_IdAndId(apartmentId, buyingId))
                .thenReturn(Optional.of(buying));

        buyingService.deleteOne(apartmentId, user, buyingId);

        verify(buyingRepository).findByCreatedBy_Apartment_IdAndId(apartmentId, buyingId);
        verify(buyingRepository).delete(buying);
    }

    @Test
    void shouldDeleteOneIfUserIsAdminAndNotBuyingCreator() {
        Integer apartmentId = 1;
        Long buyingId = 10L;

        User user = mock(User.class);

        Profile profile = mock(Profile.class);
        when(profile.getRole()).thenReturn(Role.ADMIN);
        when(user.getCurrentProfile()).thenReturn(profile);

        Profile createdBy = mock(Profile.class);

        Buying buying = new Buying();
        ReflectionTestUtils.setField(buying, "createdBy", createdBy);

        when(buyingRepository.findByCreatedBy_Apartment_IdAndId(apartmentId, buyingId))
                .thenReturn(Optional.of(buying));

        buyingService.deleteOne(apartmentId, user, buyingId);

        verify(buyingRepository).findByCreatedBy_Apartment_IdAndId(apartmentId, buyingId);
        verify(buyingRepository).delete(buying);
    }

    @Test
    void shouldDeleteOneIfUserIsApartmentCreatorAndNotBuyingCreator() {
        Integer apartmentId = 1;
        Long buyingId = 10L;

        User user = mock(User.class);

        Profile profile = mock(Profile.class);
        when(profile.getRole()).thenReturn(Role.CREATOR);
        when(user.getCurrentProfile()).thenReturn(profile);

        Profile createdBy = mock(Profile.class);

        Buying buying = new Buying();
        ReflectionTestUtils.setField(buying, "createdBy", createdBy);

        when(buyingRepository.findByCreatedBy_Apartment_IdAndId(apartmentId, buyingId))
                .thenReturn(Optional.of(buying));

        buyingService.deleteOne(apartmentId, user, buyingId);

        verify(buyingRepository).findByCreatedBy_Apartment_IdAndId(apartmentId, buyingId);
        verify(buyingRepository).delete(buying);
    }

    @Test
    void shouldThrowOnDeleteOneIfNotFound() {
        Integer apartmentId = 1;
        Long buyingId = 10L;

        User user = mock(User.class);

        when(buyingRepository.findByCreatedBy_Apartment_IdAndId(apartmentId, buyingId))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> buyingService.deleteOne(apartmentId, user, buyingId));

        verify(buyingRepository).findByCreatedBy_Apartment_IdAndId(apartmentId, buyingId);
        verify(buyingRepository, never()).delete(any(Buying.class));
    }

    @Test
    void shouldThrowOnDeleteOneIfUserIsInhabitantAndNotBuyingCreator() {
        Integer apartmentId = 1;
        Long buyingId = 10L;

        User user = mock(User.class);

        Profile profile = mock(Profile.class);
        when(profile.getId()).thenReturn(1L);
        when(profile.getRole()).thenReturn(Role.INHABITANT);
        when(user.getCurrentProfile()).thenReturn(profile);

        Profile creator = mock(Profile.class);
        when(creator.getId()).thenReturn(2L);

        Buying buying = new Buying();
        ReflectionTestUtils.setField(buying, "createdBy", creator);

        when(buyingRepository.findByCreatedBy_Apartment_IdAndId(apartmentId, buyingId))
                .thenReturn(Optional.of(buying));

        assertThrows(AccessForbiddenException.class,
                () -> buyingService.deleteOne(apartmentId, user, buyingId));

        verify(buyingRepository).findByCreatedBy_Apartment_IdAndId(apartmentId, buyingId);
        verify(buyingRepository, never()).delete(any(Buying.class));
    }

    @Test
    void shouldDeleteManyIfUserIsBuyingCreator() {
        Integer apartmentId = 1;
        List<Long> ids = List.of(10L, 11L);

        User user = mock(User.class);

        Profile profile = mock(Profile.class);
        when(profile.getId()).thenReturn(1L);
        when(profile.getRole()).thenReturn(Role.INHABITANT);
        when(user.getCurrentProfile()).thenReturn(profile);

        Profile creator = mock(Profile.class);
        when(creator.getId()).thenReturn(1L);

        Buying b1 = new Buying();
        Buying b2 = new Buying();

        ReflectionTestUtils.setField(b1, "createdBy", creator);
        ReflectionTestUtils.setField(b2, "createdBy", creator);

        when(buyingRepository.findAllByCreatedBy_Apartment_IdAndIdIn(apartmentId, ids))
                .thenReturn(List.of(b1, b2));

        buyingService.deleteMany(user, apartmentId, ids);

        verify(buyingRepository).deleteAll(List.of(b1, b2));
    }

    @Test
    void shouldDeleteManyIfUserIsAdminAndNotBuyingCreator() {
        Integer apartmentId = 1;
        List<Long> ids = List.of(10L, 11L);

        User user = mock(User.class);

        Profile profile = mock(Profile.class);
        when(profile.getRole()).thenReturn(Role.ADMIN);
        when(user.getCurrentProfile()).thenReturn(profile);

        Buying b1 = new Buying();
        Buying b2 = new Buying();

        when(buyingRepository.findAllByCreatedBy_Apartment_IdAndIdIn(apartmentId, ids))
                .thenReturn(List.of(b1, b2));

        buyingService.deleteMany(user, apartmentId, ids);

        verify(buyingRepository).deleteAll(List.of(b1, b2));
    }

    @Test
    void shouldDeleteManyIfUserIsApartmentCreatorAndNotBuyingCreator() {
        Integer apartmentId = 1;
        List<Long> ids = List.of(10L, 11L);

        User user = mock(User.class);

        Profile profile = mock(Profile.class);
        when(profile.getRole()).thenReturn(Role.CREATOR);
        when(user.getCurrentProfile()).thenReturn(profile);

        Buying b1 = new Buying();
        Buying b2 = new Buying();

        when(buyingRepository.findAllByCreatedBy_Apartment_IdAndIdIn(apartmentId, ids))
                .thenReturn(List.of(b1, b2));

        buyingService.deleteMany(user, apartmentId, ids);

        verify(buyingRepository).deleteAll(List.of(b1, b2));
    }

    @Test
    void shouldThrowOnDeleteManyIfUserIsInhabitantAndNotBuyingCreatorAtLeastAtOne() {
        Integer apartmentId = 1;
        List<Long> ids = List.of(10L, 11L);

        User user = mock(User.class);

        Profile profile = mock(Profile.class);
        when(profile.getId()).thenReturn(1L);
        when(profile.getRole()).thenReturn(Role.INHABITANT);
        when(user.getCurrentProfile()).thenReturn(profile);

        Profile creator1 = mock(Profile.class);
        when(creator1.getId()).thenReturn(2L);

        Profile creator2 = mock(Profile.class);

        Buying b1 = new Buying();
        Buying b2 = new Buying();

        ReflectionTestUtils.setField(b1, "createdBy", creator1);
        ReflectionTestUtils.setField(b2, "createdBy", creator2);

        when(buyingRepository.findAllByCreatedBy_Apartment_IdAndIdIn(apartmentId, ids))
                .thenReturn(List.of(b1, b2));

        assertThrows(AccessForbiddenException.class,
                () -> buyingService.deleteMany(user, apartmentId, ids));

        verify(buyingRepository).findAllByCreatedBy_Apartment_IdAndIdIn(apartmentId, ids);
        verify(buyingRepository, never()).deleteAll(any());
    }

}
