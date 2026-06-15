package com.example.backend.services;

import com.example.backend.dtos.in.expenses.CreateExpenseRequest;
import com.example.backend.dtos.out.expenses.CreateExpenseResponse;
import com.example.backend.dtos.out.expenses.ExpenseDto;
import com.example.backend.entities.*;
import com.example.backend.exceptions.AccessForbiddenException;
import com.example.backend.exceptions.ResourceNotFoundException;
import com.example.backend.intefaces.ExpenseNotificationHandler;
import com.example.backend.intefaces.FileStorage;
import com.example.backend.repositories.ApartmentRepository;
import com.example.backend.repositories.ExpenseRepository;
import com.example.backend.types.Color;
import com.example.backend.types.ExpenseCategory;
import com.example.backend.types.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.Month;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private FileStorage fileStorage;

    @Mock
    private ExpenseNotificationHandler expenseNotificationHandler;

    @Mock
    private ApartmentRepository apartmentRepository;

    @InjectMocks
    private ExpenseService expenseService;

    @Test
    void shouldCreateWithCheck() {
        Integer apartmentId = 1;

        User user = mock(User.class);

        Profile profile = new Profile();
        when(user.getCurrentProfile()).thenReturn(profile);

        Apartment apartment = new Apartment();

        CreateExpenseRequest dto = new CreateExpenseRequest(
                "Продукты",
                1000,
                ExpenseCategory.PRODUCTS
        );

        String checkImageName = "check.png";

        Long expenseId = 1L;

        String signedUrl = "https://signed-url";

        when(apartmentRepository.findById(apartmentId)).thenReturn(Optional.of(apartment));

        when(expenseRepository.save(any(Expense.class))).thenAnswer(inv -> {
                Expense expense = inv.getArgument(0);
                ReflectionTestUtils.setField(expense, "id", expenseId);
                return expense;
        });

        when(fileStorage.getPresignedUrl(anyString(), eq(checkImageName))).thenReturn(signedUrl);

        CreateExpenseResponse response = expenseService.create(user, apartmentId, dto, checkImageName);

        assertEquals(expenseId, response.id());
        assertEquals(signedUrl, response.checkImageUrl());

        verify(apartmentRepository).findById(apartmentId);
        verify(expenseRepository).save(any(Expense.class));
        verify(expenseNotificationHandler).handleExpenseCreate(eq(user), any(Expense.class));
        verify(fileStorage).getPresignedUrl(anyString(), eq(checkImageName));
    }

    @Test
    void shouldCreateWithoutCheck() {
        Integer apartmentId = 1;

        User user = mock(User.class);

        Profile profile = new Profile();
        when(user.getCurrentProfile()).thenReturn(profile);

        Apartment apartment = new Apartment();

        CreateExpenseRequest dto = new CreateExpenseRequest(
                "Продукты",
                1000,
                ExpenseCategory.PRODUCTS
        );

        when(apartmentRepository.findById(apartmentId))
                .thenReturn(Optional.of(apartment));

        when(expenseRepository.save(any(Expense.class)))
                .thenAnswer(inv -> {
                    Expense expense = inv.getArgument(0);
                    ReflectionTestUtils.setField(expense, "id", 1L);
                    return expense;
                });

        CreateExpenseResponse response =
                expenseService.create(user, apartmentId, dto, null);

        assertEquals(1L, response.id());
        assertNull(response.checkImageUrl());

        verify(apartmentRepository).findById(apartmentId);
        verify(expenseRepository).save(any(Expense.class));
        verify(expenseNotificationHandler)
                .handleExpenseCreate(eq(user), any(Expense.class));

        verify(fileStorage, never())
                .getPresignedUrl(anyString(), anyString());

        verify(fileStorage, never())
                .delete(anyString(), anyString());
    }

    @Test
    void shouldThrowOnCreateIfApartmentNotFoundAndDeleteImage() {
        Integer apartmentId = 1;

        User user = mock(User.class);

        CreateExpenseRequest dto = new CreateExpenseRequest(
                "Продукты",
                1000,
                ExpenseCategory.PRODUCTS
        );

        String checkImageName = "check.png";

        when(apartmentRepository.findById(apartmentId))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> expenseService.create(user, apartmentId, dto, checkImageName));

        verify(apartmentRepository).findById(apartmentId);
        verify(expenseRepository, never()).save(any());
        verifyNoInteractions(expenseNotificationHandler);

        verify(fileStorage).delete(anyString(), eq(checkImageName));
    }

    @Test
    void shouldThrowOnCreateIfApartmentNotFoundAndNotDeleteImage() {
        Integer apartmentId = 1;

        User user = mock(User.class);

        CreateExpenseRequest dto = new CreateExpenseRequest(
                "Продукты",
                1000,
                ExpenseCategory.PRODUCTS
        );

        when(apartmentRepository.findById(apartmentId))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> expenseService.create(user, apartmentId, dto, null));

        verify(apartmentRepository).findById(apartmentId);

        verify(expenseRepository, never()).save(any());

        verifyNoInteractions(expenseNotificationHandler);

        verify(fileStorage, never()).delete(anyString(), anyString());
    }

    @Test
    void shouldGet() {
        Integer apartmentId = 1;
        Short page = 0;
        Short size = 10;

        Long profileId = 1L;
        ExpenseCategory category = ExpenseCategory.PRODUCTS;
        YearMonth period = YearMonth.of(2026, 1);

        Profile profile = mock(Profile.class);
        when(profile.getId()).thenReturn(1L);
        when(profile.getName()).thenReturn("Марк");
        when(profile.getAvatarColor()).thenReturn(Color.BLUE);

        String checkImageName = "check.png";

        Expense expenseWithCheck = mock(Expense.class);
        when(expenseWithCheck.getCreatedBy()).thenReturn(profile);
        when(expenseWithCheck.getCheckImageName()).thenReturn(checkImageName);
        when(expenseWithCheck.getCreatedAt()).thenReturn(Instant.now());

        Expense expenseWithoutCheck = mock(Expense.class);
        when(expenseWithoutCheck.getCreatedBy()).thenReturn(profile);
        when(expenseWithoutCheck.getCheckImageName()).thenReturn(null);
        when(expenseWithoutCheck.getCreatedAt()).thenReturn(Instant.now());

        Page<Expense> expensePage = new PageImpl<>(
                List.of(expenseWithCheck, expenseWithoutCheck)
        );

        when(expenseRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expensePage);

        when(fileStorage.getPresignedUrl(anyString(), eq(checkImageName))).thenReturn("https://signed-url");

        List<ExpenseDto> result = expenseService.get(
                apartmentId,
                page,
                size,
                profileId,
                category,
                period
        );

        assertEquals(2, result.size());

        verify(expenseRepository).findAll(any(Specification.class), any(Pageable.class));

        verify(fileStorage).getPresignedUrl(anyString(), eq(checkImageName));
    }

    @Test
    void shouldGetAmount() {
        Integer apartmentId = 1;

        Integer expenseAmount = 1000;

        Integer result = expenseService.getAmount(apartmentId);

        assertEquals(expenseAmount, result);
    }

    @Test
    void shouldReturnZeroIfAmountIsNull() {
        Integer apartmentId = 1;

        Integer result = expenseService.getAmount(apartmentId);

        assertEquals(0, result);
    }

    @Test
    void shouldGetSumByCategory() {
        Integer apartmentId = 1;
        YearMonth period = YearMonth.of(2026, 1);

        when(expenseRepository.getAmountSumByApartmentIdAndCategoryAndCreatedAtBetween(
                eq(apartmentId),
                any(ExpenseCategory.class),
                any(Instant.class),
                any(Instant.class)
        )).thenAnswer(invocation -> {
            ExpenseCategory category = invocation.getArgument(1);

            if (category == ExpenseCategory.PRODUCTS) {
                return 100;
            }

            return null;
        });

        Map<ExpenseCategory, Integer> result =
                expenseService.getSumByCategory(apartmentId, period);

        assertEquals(1, result.size());
        assertEquals(100, result.get(ExpenseCategory.PRODUCTS));
        assertFalse(result.containsKey(ExpenseCategory.RENT));
    }

    @Test
    void shouldDeleteMany() {
        Integer apartmentId = 1;

        User user = mock(User.class);

        Profile profile = mock(Profile.class);
        when(profile.getId()).thenReturn(1L);
        when(user.getCurrentProfile()).thenReturn(profile);

        Profile creator = mock(Profile.class);
        when(creator.getId()).thenReturn(1L);

        Expense expense1 = mock(Expense.class);
        Expense expense2 = mock(Expense.class);

        when(expense1.getCreatedBy()).thenReturn(creator);
        when(expense1.getCreatedAt()).thenReturn(Instant.now());
        when(expense1.getAmount()).thenReturn(100);
        when(expense1.getCategory()).thenReturn(ExpenseCategory.PRODUCTS);
        when(expense1.getCheckImageName()).thenReturn("check.png");

        when(expense2.getCreatedBy()).thenReturn(creator);
        when(expense2.getCreatedAt()).thenReturn(Instant.now());
        when(expense2.getAmount()).thenReturn(200);
        when(expense2.getCategory()).thenReturn(ExpenseCategory.PRODUCTS);
        when(expense2.getCheckImageName()).thenReturn(null);

        when(expenseRepository.findAllByCreatedBy_Apartment_IdAndIdIn(apartmentId, List.of(1L, 2L)))
                .thenReturn(List.of(expense1, expense2));

        doNothing().when(fileStorage).deleteMany(anyString(), anyList());

        expenseService.deleteMany(user, apartmentId, List.of(1L, 2L));

        verify(expenseRepository).findAllByCreatedBy_Apartment_IdAndIdIn(apartmentId, List.of(1L, 2L));

        verify(fileStorage).deleteMany(anyString(), anyList());

        verify(expenseRepository).deleteAll(anyList());
    }

    @Test
    void shouldThrowOnDeleteManyIfUserIsInhabitantAndNotExpenseCreatorAtLeastInOne() {
        Integer apartmentId = 1;

        User user = mock(User.class);

        Long userProfileId = 1L;
        Long otherUserProfileId = 2L;

        Profile profile = mock(Profile.class);
        when(profile.getId()).thenReturn(1L);
        when(profile.getRole()).thenReturn(Role.INHABITANT);
        when(user.getCurrentProfile()).thenReturn(profile);

        Profile creator1 = mock(Profile.class);
        when(creator1.getId()).thenReturn(userProfileId);

        Profile creator2 = mock(Profile.class);
        when(creator2.getId()).thenReturn(otherUserProfileId);

        Expense expense1 = mock(Expense.class);
        Expense expense2 = mock(Expense.class);

        when(expense1.getCreatedBy()).thenReturn(creator1);
        when(expense1.getCreatedAt()).thenReturn(Instant.now());
        when(expense1.getAmount()).thenReturn(100);
        when(expense1.getCategory()).thenReturn(ExpenseCategory.PRODUCTS);

        when(expense2.getCreatedBy()).thenReturn(creator2);

        when(expenseRepository.findAllByCreatedBy_Apartment_IdAndIdIn(
                apartmentId,
                List.of(userProfileId, otherUserProfileId)
        )).thenReturn(List.of(expense1, expense2));

        assertThrows(AccessForbiddenException.class,
                () -> expenseService.deleteMany(user, apartmentId, List.of(userProfileId, otherUserProfileId)));

        verify(expenseRepository)
                .findAllByCreatedBy_Apartment_IdAndIdIn(apartmentId, List.of(userProfileId, otherUserProfileId));

        verify(fileStorage, never()).deleteMany(anyString(), anyList());
        verify(expenseRepository, never()).deleteAll(anyList());
    }

}
