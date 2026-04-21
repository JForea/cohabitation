package com.example.backend.dtos.in.buyings;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateManyBuyingsDto(
        @NotNull
        @Size(min = 1, max = 50)
        List<CreateBuyingShortDto> buyings,
        Long assignedTo,
        @NotNull
        Boolean isPublic
) {}
