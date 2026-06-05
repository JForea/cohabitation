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
import com.example.backend.specifications.ExpenseSpecifications;
import com.example.backend.types.ExpenseCategory;
import com.example.backend.types.Role;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;

@Service
public class ExpenseService {

    private static final Logger log = LoggerFactory.getLogger(ExpenseService.class);
    private final ExpenseRepository expenseRepository;

    private final FileStorage fileStorage;

    private final ExpenseNotificationHandler expenseNotificationHandler;

    private final ApartmentRepository apartmentRepository;

    private final String bucketName = "checks";

    public ExpenseService(ExpenseRepository expenseRepository,
                          FileStorage fileStorage,
                          ExpenseNotificationHandler expenseNotificationHandler,
                          ApartmentRepository apartmentRepository) {
        this.expenseRepository = expenseRepository;
        this.fileStorage = fileStorage;
        this.expenseNotificationHandler = expenseNotificationHandler;
        this.apartmentRepository = apartmentRepository;
    }

    @Transactional
    public CreateExpenseResponse create(
            User user,
            Integer apartmentId,
            CreateExpenseRequest dto,
            String checkImageName) {
        try {
            Apartment apartment = apartmentRepository.findById(apartmentId).orElseThrow(
                    () -> new ResourceNotFoundException("Apartment not found.")
            );

            Expense expense = new Expense(
                    apartment,
                    dto.name(),
                    dto.amount(),
                    dto.category(),
                    checkImageName,
                    user.getCurrentProfile()
            );

            expenseRepository.save(expense);

            expenseNotificationHandler.handleExpenseCreate(user, expense);

            return new CreateExpenseResponse(
                    expense.getId(),
                    checkImageName != null ? fileStorage.getPresignedUrl(bucketName, checkImageName) : null
            );
        } catch (Exception e) {
            log.error("Creating expense error: ", e);
            if (checkImageName != null)
                fileStorage.delete(bucketName, checkImageName);
            throw e;
        }
    }

    @Transactional
    public List<ExpenseDto> get(
            Integer apartmentId,
            Short page,
            Short size,
            Long profileId,
            ExpenseCategory category,
            YearMonth period
    ) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Specification<Expense> spec =
                Specification.where(ExpenseSpecifications.byApartmentId(apartmentId))
                        .and(ExpenseSpecifications.byProfileId(profileId))
                        .and(ExpenseSpecifications.byCategory(category))
                        .and(ExpenseSpecifications.byCategory(category))
                        .and(ExpenseSpecifications.byPeriod(period));

        return expenseRepository.findAll(spec, pageable)
                .map(expense -> {
                    String checkImageName = expense.getCheckImageName();

                    return new ExpenseDto(
                            expense,
                            checkImageName != null
                                    ? fileStorage.getPresignedUrl(bucketName, checkImageName)
                                    : null
                    );
                }).toList();
    }

    public Integer getAmount(Integer apartmentId) {
        Apartment apartment = apartmentRepository.findById(apartmentId).orElseThrow(() ->
                new ResourceNotFoundException("Apartment not found.")
        );

        YearMonth currentMonth = YearMonth.now();

        ZoneOffset offset = ZoneOffset.ofTotalSeconds(
                apartment.getMinutesOffset() * 60
        );

        Instant start = currentMonth
                .atDay(1)
                .atStartOfDay(offset)
                .toInstant();

        Instant end = currentMonth
                .plusMonths(1)
                .atDay(1)
                .atStartOfDay(offset)
                .toInstant();

        Integer amount = expenseRepository.getAmountSumByApartmentIdAndCreatedAtInPeriod(
                apartmentId,
                start,
                end
        );

        return amount != null ? amount : 0;
    }

    public Map<ExpenseCategory, Integer> getSumByCategory(
            Integer apartmentId,
            YearMonth period
    ) {
        Apartment apartment = apartmentRepository.findById(apartmentId)
                .orElseThrow();

        ZoneOffset offset = ZoneOffset.ofTotalSeconds(
                apartment.getMinutesOffset() * 60
        );

        Instant start = period
                .atDay(1)
                .atStartOfDay(offset)
                .toInstant();

        Instant end = period
                .plusMonths(1)
                .atDay(1)
                .atStartOfDay(offset)
                .toInstant();

        Map<ExpenseCategory, Integer> result = new HashMap<>();

        for (ExpenseCategory category : ExpenseCategory.values()) {
            Integer expenseAmount =
                    expenseRepository.getAmountSumByApartmentIdAndCategoryAndCreatedAtBetween(
                            apartmentId,
                            category,
                            start,
                            end
                    );

            if (expenseAmount != null) {
                result.put(category, expenseAmount);
            }
        }

        return result;
    }

    private record SubtractKey(
       Long profileId,
       ExpenseCategory category
    ) {}

    @Transactional
    public void deleteMany(User user, Integer apartmentId, List<Long> ids) {
        List<Expense> expenses = expenseRepository.findAllByCreatedBy_Apartment_IdAndIdIn(apartmentId, ids);
        Profile profile = user.getCurrentProfile();

        Instant now = Instant.now();

        List<String> filenames = new ArrayList<>();
        Map<SubtractKey, Integer> subtractValues = new HashMap<>();
        for (Expense expense : expenses) {
            if ((!Objects.equals(expense.getCreatedBy().getId(), profile.getId()) ||
                    now.getEpochSecond() - expense.getCreatedAt().getEpochSecond() > 3600) &&
                    profile.getRole() == Role.INHABITANT) {
                throw new AccessForbiddenException("Request contains expenses which you can't delete.");
            }
            if (expense.getCheckImageName() != null)
                filenames.add(expense.getCheckImageName());

            SubtractKey key = new SubtractKey(expense.getCreatedBy().getId(), expense.getCategory());

            subtractValues.putIfAbsent(key, 0);
            subtractValues.put(key, subtractValues.get(key) + expense.getAmount());
        }

        fileStorage.deleteMany(bucketName, filenames);

        expenseRepository.deleteAll(expenses);
    }
}
