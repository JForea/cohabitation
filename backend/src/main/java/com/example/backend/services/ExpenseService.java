package com.example.backend.services;

import com.example.backend.dtos.in.expenses.CreateExpenseRequest;
import com.example.backend.dtos.out.expenses.CreateExpenseResponse;
import com.example.backend.dtos.out.expenses.ExpenseDto;
import com.example.backend.entities.Expense;
import com.example.backend.entities.ProfileMonthlyExpense;
import com.example.backend.entities.User;
import com.example.backend.intefaces.ExpenseNotificationHandler;
import com.example.backend.intefaces.FileStorage;
import com.example.backend.repositories.ExpenseRepository;
import com.example.backend.repositories.ProfileMonthlyExpenseRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Month;
import java.time.Year;
import java.util.Calendar;
import java.util.List;

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

            expenseNotificationHandler.handleExpenseNotification(user, expense, true);

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
}
