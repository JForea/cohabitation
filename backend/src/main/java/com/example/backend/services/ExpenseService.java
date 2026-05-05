package com.example.backend.services;

import com.example.backend.dtos.in.expenses.CreateExpenseRequest;
import com.example.backend.dtos.out.expenses.CreateExpenseResponse;
import com.example.backend.dtos.out.expenses.ExpenseDto;
import com.example.backend.entities.Apartment;
import com.example.backend.entities.Expense;
import com.example.backend.entities.User;
import com.example.backend.exceptions.ResourceNotFoundException;
import com.example.backend.intefaces.FileStorage;
import com.example.backend.repositories.ApartmentRepository;
import com.example.backend.repositories.ExpenseRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    private final ApartmentRepository apartmentRepository;

    private final FileStorage fileStorage;

    private final String bucketName = "checks";

    public ExpenseService(ExpenseRepository expenseRepository,
                          ApartmentRepository apartmentRepository,
                          FileStorage fileStorage) {
        this.expenseRepository = expenseRepository;
        this.apartmentRepository = apartmentRepository;
        this.fileStorage = fileStorage;
    }

    public CreateExpenseResponse create(
            Integer apartmentId,
            User user,
            CreateExpenseRequest dto,
            String checkImageName) {
        try {
            Apartment apartment = apartmentRepository.findById(apartmentId).orElseThrow(
                    () -> new ResourceNotFoundException("Apartment not found.")
            );

            Expense expense = new Expense(
                    apartment,
                    dto.name(),
                    dto.sum(),
                    dto.category(),
                    checkImageName,
                    user.getCurrentProfile()
            );

            expenseRepository.save(expense);

            return new CreateExpenseResponse(
                    expense.getId(),
                    fileStorage.getPresignedUrl(bucketName, checkImageName)
            );
        } catch (Exception e) {
            fileStorage.delete(bucketName, checkImageName);
            throw e;
        }
    }

    public List<ExpenseDto> get(Integer apartmentId, Short page, Short size) {
        return expenseRepository.findAllByApartment_Id(apartmentId, PageRequest.of(page, size))
                .map(
                        expense -> new ExpenseDto(expense, fileStorage.getPresignedUrl(
                                bucketName,
                                expense.getCheckImageName()
                        ))
                ).toList();
    }
}
