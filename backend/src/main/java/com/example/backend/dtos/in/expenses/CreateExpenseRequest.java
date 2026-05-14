package com.example.backend.dtos.in.expenses;

import com.example.backend.types.ExpenseCategory;
import jakarta.validation.constraints.*;

public record CreateExpenseRequest (
    @NotBlank
    @Size(min = 2, max = 64)
    String name,
    @NotNull
    @Min(1) @Max(1000000)
    Integer amount,
    @NotNull
    ExpenseCategory category
) {}
