package com.example.backend.dtos.out.expenses;

import com.example.backend.entities.Expense;

public record CreateExpenseResponse(
    Long id,
    String imageUrl
) {
    public CreateExpenseResponse(Expense expense) {
        this(
                expense.getId(),
                expense.getCheckImageName()
        );
    }
}
