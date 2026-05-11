package com.example.backend.services;

import com.example.backend.dtos.in.expenses.CreateExpenseRequest;
import com.example.backend.dtos.out.expenses.CreateExpenseResponse;
import com.example.backend.dtos.out.expenses.ExpenseDto;
import com.example.backend.entities.Expense;
import com.example.backend.entities.Profile;
import com.example.backend.entities.ProfileMonthlyExpense;
import com.example.backend.entities.User;
import com.example.backend.exceptions.AccessForbiddenException;
import com.example.backend.intefaces.ExpenseNotificationHandler;
import com.example.backend.intefaces.FileStorage;
import com.example.backend.repositories.ExpenseRepository;
import com.example.backend.repositories.ProfileMonthlyExpenseRepository;
import com.example.backend.types.Role;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.Month;
import java.time.Year;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

@Service
public class ExpenseService {

    private static final Logger log = LoggerFactory.getLogger(ExpenseService.class);
    private final ExpenseRepository expenseRepository;

    private final FileStorage fileStorage;

    private final ProfileMonthlyExpenseRepository profileMonthlyExpenseRepository;

    private final ExpenseNotificationHandler expenseNotificationHandler;

    private final String bucketName = "checks";

    public ExpenseService(ExpenseRepository expenseRepository,
                          FileStorage fileStorage,
                          ProfileMonthlyExpenseRepository profileMonthlyExpenseRepository,
                          ExpenseNotificationHandler expenseNotificationHandler) {
        this.expenseRepository = expenseRepository;
        this.fileStorage = fileStorage;
        this.profileMonthlyExpenseRepository = profileMonthlyExpenseRepository;
        this.expenseNotificationHandler = expenseNotificationHandler;
    }

    @Transactional
    public CreateExpenseResponse create(
            User user,
            CreateExpenseRequest dto,
            String checkImageName) {
        try {
            Expense expense = new Expense(
                    dto.name(),
                    dto.amount(),
                    dto.category(),
                    checkImageName,
                    user.getCurrentProfile()
            );

            expenseRepository.save(expense);

            Calendar calendar = Calendar.getInstance();

            Month month = Month.of(calendar.get(Calendar.MONTH));
            Year year = Year.of(calendar.get(Calendar.YEAR));

            ProfileMonthlyExpense monthlyExpense =
                    profileMonthlyExpenseRepository.findByYearAndMonthAndProfileAndExpenseCategory(
                            year.getValue(),
                            month,
                            user.getCurrentProfile(),
                            dto.category()
                    ).orElse(new ProfileMonthlyExpense(
                            user.getCurrentProfile(),
                            dto.category(),
                            year,
                            month
                    ));

            monthlyExpense.addAmount(expense.getAmount());

            profileMonthlyExpenseRepository.save(monthlyExpense);

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
    public List<ExpenseDto> get(Integer apartmentId, Short page, Short size) {
        return expenseRepository.findAllByCreatedBy_Apartment_Id(apartmentId, PageRequest.of(page, size))
                .map(
                        expense -> {
                            String checkImageName = expense.getCheckImageName();
                            return new ExpenseDto(
                                    expense,
                                    checkImageName != null
                                            ? fileStorage.getPresignedUrl(bucketName, checkImageName)
                                            : null
                            );
                        }
                ).toList();
    }

    public Integer getAmount(Integer apartmentId) {
        Calendar calendar = Calendar.getInstance();

        Month month = Month.of(calendar.get(Calendar.MONTH));
        Year year = Year.of(calendar.get(Calendar.YEAR));

        return profileMonthlyExpenseRepository.getSumByApartmentIdAndYearAndMonth(
                apartmentId,
                year.getValue(),
                month
        );
    }

    @Transactional
    public void deleteMany(User user, Integer apartmentId, List<Long> ids) {
        List<Expense> expenses = expenseRepository.findAllByCreatedBy_Apartment_IdAndIdIn(apartmentId, ids);
        Profile profile = user.getCurrentProfile();

        if (profile.getRole() != Role.INHABITANT) {
            expenseRepository.deleteAll(expenses);
            expenseNotificationHandler.handleManyExpensesDelete(user, expenses);
            return;
        }

        Instant now = Instant.now();

        List<String> filenames = new ArrayList<>();
        for (Expense expense : expenses) {
            if ((!Objects.equals(expense.getCreatedBy().getId(), profile.getId()) ||
                    now.getEpochSecond() - expense.getCreatedAt().getEpochSecond() > 3600) &&
                    profile.getRole() == Role.INHABITANT) {
                throw new AccessForbiddenException("Request contains expenses which you can't delete.");
            }
            if (expense.getCheckImageName() != null)
                filenames.add(expense.getCheckImageName());
        }

        fileStorage.deleteMany(bucketName, filenames);

        expenseRepository.deleteAll(expenses);
    }
}
