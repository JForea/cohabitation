package com.example.backend.dtos.out.expenses;

import com.example.backend.dtos.out.profile.ProfileDto;
import com.example.backend.entities.Expense;
import com.example.backend.types.ExpenseCategory;

public record ExpenseDto(
        Long id,
        String name,
        Integer sum,
        ExpenseCategory category,
        String checkImageUrl,
        ProfileDto profile) {
    public ExpenseDto(Expense expense, String checkImageRef) {
        this(
                expense.getId(),
                expense.getName(),
                expense.getSum(),
                expense.getCategory(),
                checkImageRef,
                new ProfileDto(expense.getCreatedBy())
        );
    }
}
