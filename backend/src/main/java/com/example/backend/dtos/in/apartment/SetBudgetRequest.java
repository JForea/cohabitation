package com.example.backend.dtos.in.apartment;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record SetBudgetRequest(
        @NotNull @Min(0) @Max(1000000)
        Integer budget
) {}
