package com.example.backend.dtos.out.expenses;

import com.example.backend.dtos.out.profile.ProfileBriefDto;
import com.example.backend.dtos.out.profile.ProfileDto;
import com.example.backend.entities.Expense;
import com.example.backend.types.ExpenseCategory;

import java.time.LocalDate;
import java.time.ZoneId;

public record ExpenseDto(
        Long id,
        String name,
        Integer sum,
        ExpenseCategory category,
        String checkImageUrl,
        ProfileBriefDto createdBy,
        LocalDate createdAt
) {
    public ExpenseDto(Expense expense, String checkImageRef) {
        this(
                expense.getId(),
                expense.getName(),
                expense.getAmount(),
                expense.getCategory(),
                checkImageRef,
                new ProfileBriefDto(expense.getCreatedBy()),
                expense.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDate()
        );
    }
}
