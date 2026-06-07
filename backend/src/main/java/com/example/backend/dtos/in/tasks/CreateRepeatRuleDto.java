package com.example.backend.dtos.in.tasks;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.List;

public record CreateRepeatRuleDto(
        @NotNull
        @Min(1) @Max(365)
        Short intervalDays,
        @Future
        LocalDate endDate,
        @NotNull
        @NotEmpty
        List<Long> assignedIds
) {
}
