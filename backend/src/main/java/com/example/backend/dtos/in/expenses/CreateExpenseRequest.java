package com.example.backend.dtos.in.expenses;

import com.example.backend.types.ExpenseCategory;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateExpenseRequest (
    @NotBlank
    @Size(min = 2, max = 64)
    String name,
    @NotNull
    @Min(1)
    Integer sum,
    @NotNull
    ExpenseCategory category
) {}
