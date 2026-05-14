package com.example.backend.dtos.in.apartment;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record SetBudgetRequest(
        @Min(0) @Max(1000000)
        Integer budget
) {}
